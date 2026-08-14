import { Dispatch, SetStateAction, useCallback, useEffect, useRef, useState } from "react";
import axios from "axios";

// Flat page shape returned by the backend's PageResponse<T>: `page` is the
// current page index (a number), `last` a boolean. NOT Spring Data's default
// nested-page object — treating `page` as an object breaks paging (on page 0,
// `page` is 0/falsy, which wrongly reads as "last page").
type PageResponse<R> = {
    content: R[];
    page: number;
    totalPages: number;
    last: boolean;
};

type Options<R, T> = {
    /** Endpoint URL without paging params, e.g. `${API_BASE}/customers`. */
    url: string;
    /** Query params for the current filters/sort, WITHOUT page/size. */
    params: URLSearchParams;
    /** Stable id for de-duplicating rows across pages. */
    getId: (item: T) => string | number;
    /** Map a raw response row to a domain item. Defaults to identity. */
    map?: (raw: R) => T;
    /** Page size (default 40). */
    size?: number;
    /** When false, skip fetching entirely (e.g. missing permission). */
    enabled?: boolean;
};

/**
 * Scroll-based infinite loading against a paginated `PageResponse<T>` endpoint.
 * Owns the paging mechanics shared by list pages: a de-duplicated accumulator,
 * a concurrency guard, reset-on-filter-change, and a "load next page when within
 * 30% of the bottom" scroll listener. Attach the returned `scrollRef` to the
 * scrollable container.
 *
 * The caller supplies the request-specific bits (url, params, row mapping, id).
 * Changing `params`/`url`/`size` resets and refetches from page 0.
 */
export function useInfiniteScroll<R, T = R>(
    { url, params, getId, map, size = 40, enabled = true }: Options<R, T>,
) {
    const [items, setItems] = useState<T[]>([]);
    const [loading, setLoading] = useState(false);
    const [nonce, setNonce] = useState(0); // bumped by reload() to force a refetch
    const scrollRef = useRef<HTMLDivElement>(null);

    const page = useRef(0);
    const isLastPage = useRef(false);
    const isFetching = useRef(false);
    const seen = useRef(new Set<string | number>());
    const abort = useRef<AbortController | null>(null);

    // params is rebuilt every render; serialize it so effect deps are stable by value.
    const key = params.toString();

    const fetchPage = useCallback((pg: number, showLoading: boolean) => {
        if (!enabled || isFetching.current || isLastPage.current) return;
        isFetching.current = true;
        if (showLoading) setLoading(true);

        const controller = new AbortController();
        abort.current = controller;

        const q = new URLSearchParams(key);
        q.set("page", String(pg));
        q.set("size", String(size));

        axios.get<PageResponse<R>>(`${url}?${q}`, { signal: controller.signal })
            .then(res => {
                const rows = (res.data.content ?? []).map(r => (map ? map(r) : (r as unknown as T)));
                const fresh = rows.filter(it => !seen.current.has(getId(it)));
                fresh.forEach(it => seen.current.add(getId(it)));
                setItems(prev => [...prev, ...fresh]);
                isLastPage.current = res.data.last ?? (res.data.page >= res.data.totalPages - 1);
            })
            .catch(err => { if (!axios.isCancel(err)) console.error(`Error fetching ${url}:`, err); })
            .finally(() => { isFetching.current = false; if (showLoading) setLoading(false); });
        // getId/map are treated as render-stable; deps intentionally track the request identity.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [url, key, size, enabled]);

    // Reset + load page 0 whenever the request (url/filters/sort/size) or reload() changes.
    useEffect(() => {
        abort.current?.abort();
        seen.current.clear();
        setItems([]);
        page.current = 0;
        isLastPage.current = false;
        isFetching.current = false;
        if (enabled) fetchPage(0, true);
        return () => abort.current?.abort();
    }, [fetchPage, enabled, nonce]);

    // Load the next page when scrolled within 30% of the bottom.
    useEffect(() => {
        const el = scrollRef.current;
        if (!el) return;
        const onScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3) {
                page.current += 1;
                fetchPage(page.current, false);
            }
        };
        el.addEventListener("scroll", onScroll);
        return () => el.removeEventListener("scroll", onScroll);
    }, [fetchPage]);

    // If a page didn't fill the container, no scroll event can fire — keep pulling
    // until it overflows or we reach the last page.
    useEffect(() => {
        const el = scrollRef.current;
        if (el && !isLastPage.current && !isFetching.current && el.scrollHeight <= el.clientHeight) {
            page.current += 1;
            fetchPage(page.current, false);
        }
    }, [items, fetchPage]);

    // Stable identity so callers can drive it from an effect (e.g. an app-wide
    // "data changed" signal) without re-running that effect every render.
    const reload = useCallback(() => setNonce(n => n + 1), []);

    return {
        items,
        setItems: setItems as Dispatch<SetStateAction<T[]>>,
        loading,
        scrollRef,
        reload,
    };
}
