package com.volter.shop.modules.transaction.domain.repository;

import com.volter.shop.modules.transaction.domain.model.ActivityEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Read-only: backed by the {@code activity_entry} view. */
public interface ActivityEntryRepository
        extends JpaRepository<ActivityEntry, String>, JpaSpecificationExecutor<ActivityEntry> {
}
