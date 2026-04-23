package com.volter.shop.shared.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PredicateBuilder<T> {

    private final Root<T> root;
    private final CriteriaBuilder cb;
    private final List<Predicate> predicates = new ArrayList<>();

    public PredicateBuilder(Root<T> root, CriteriaBuilder cb) {
        this.root = root;
        this.cb = cb;
    }

    // ── String ────────────────────────────────────────────────────────────────

    public PredicateBuilder<T> withStringLike(Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            predicates.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
        return this;
    }

    // ── Enum ──────────────────────────────────────────────────────────────────

    public <E extends Enum<E>> PredicateBuilder<T> withEnum(Path<E> path, E value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
        return this;
    }

    // ── Boolean ───────────────────────────────────────────────────────────────

    public PredicateBuilder<T> withBoolean(Path<Boolean> path, Boolean value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
        return this;
    }

    // ── Date Range ────────────────────────────────────────────────────────────

    public PredicateBuilder<T> withDateRange(Path<LocalDate> path, LocalDate from, LocalDate to) {
        if (from != null || to != null) {
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(path, from));
            if (to != null)   predicates.add(cb.lessThanOrEqualTo(path, to));
        }
        return this;
    }

    // ── Comparable Range ──────────────────────────────────────────────────────

    public <N extends Comparable<N>> PredicateBuilder<T> withRange(Path<N> path, N from, N to) {
        if (from != null || to != null) {
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(path, from));
            if (to != null)   predicates.add(cb.lessThanOrEqualTo(path, to));
        }
        return this;
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    public List<Predicate> build() {
        return new ArrayList<>(predicates);
    }
}
