package com.volter.platform.modules.shop.application;

import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.platform.modules.shop.application.dto.ShopCreateRequest;
import com.volter.platform.modules.shop.application.dto.ShopFilterRequest;
import com.volter.platform.modules.shop.application.dto.ShopUpdateRequest;
import com.volter.platform.modules.shop.application.dto.StaffShopAssignRequest;
import com.volter.platform.modules.shop.domain.model.Shop;
import com.volter.platform.modules.shop.domain.model.StaffShop;
import com.volter.platform.modules.shop.domain.repository.ShopRepository;
import com.volter.platform.modules.shop.domain.repository.StaffShopRepository;
import com.volter.platform.modules.shop.domain.specification.ShopSpecification;
import com.volter.shared.config.SchemaInitializer;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;
    private final StaffShopRepository staffShopRepository;
    private final StaffRepository staffRepository;
    private final SchemaInitializer schemaInitializer;

    /**
     * Lists paginated shops based on the provided filter. No access control restriction is applied.
     */
    @Transactional(readOnly = true)
    public Page<Shop> list(ShopFilterRequest filter, Pageable pageable) {
        return shopRepository.findAll(ShopSpecification.matches(filter), pageable);
    }

    /**
     * Gets a single shop by ID. No access control restriction is applied.
     */
    @Transactional(readOnly = true)
    public Shop get(Long id) {
        return loadShop(id);
    }

    /**
     * Shops for the given ids. Used by other modules to enrich responses that reference shops by id.
     */
    @Transactional(readOnly = true)
    public List<Shop> findByIds(Collection<Long> ids) {
        return shopRepository.findAllById(ids);
    }

    /**
     * Creates a new shop based on the provided request data.
     * Validates that the code and schema name are unique.
     * Initializes the tenant schema for the new shop.
     */
    public Shop create(ShopCreateRequest request) {
        if (shopRepository.existsByCode(request.code())) {
            throw new BusinessRuleException("Shop code already exists: " + request.code());
        }
        if (shopRepository.existsBySchemaName(request.schemaName())) {
            throw new BusinessRuleException("Schema name already exists: " + request.schemaName());
        }
        Shop shop = Shop.builder()
                .name(request.name())
                .code(request.code())
                .schemaName(request.schemaName())
                .address(request.address())
                .phone(request.phone())
                .build();
        Shop saved = shopRepository.save(shop);
        schemaInitializer.initializeTenantSchema(saved.getSchemaName());
        return saved;
    }

    /**
     * Updates the contact details (address and phone) of an existing shop identified by its ID.
     * Validates that the shop exists before updating.
     */
    public Shop updateContactDetails(Long id, ShopUpdateRequest request) {
        Shop shop = loadShop(id);
        shop.updateContactDetails(request.address(), request.phone());
        return shop;
    }

    /**
     * Closes the shop identified by its ID, making it inactive.
     * Validates that the shop exists before closing.
     */
    public void close(Long id) {
        loadShop(id).close();
    }

    /**
     * Reopens the shop identified by its ID, making it active again.
     * Validates that the shop exists before reopening.
     */
    public void reopen(Long id) {
        loadShop(id).reopen();
    }

    /**
     * Lists staff assignments for the shop identified by its ID.
     * Validates that the shop exists before listing assignments.
     */
    @Transactional(readOnly = true)
    public List<StaffShop> listAssignments(Long shopId) {
        loadShop(shopId);
        return staffShopRepository.findAllByShop_Id(shopId);
    }

    /**
     * Assigns a staff member to the shop identified by its ID based on the provided request data.
     * Validates that the shop and staff exist, and that the staff is not already actively assigned to the shop.
     */
    public StaffShop assignStaff(Long shopId, StaffShopAssignRequest request) {
        Shop shop = loadShop(shopId);
        if (!staffRepository.existsById(request.staffId())) {
            throw new ResourceNotFoundException("Staff not found: " + request.staffId());
        }
        staffShopRepository.findByStaffIdAndShop_Id(request.staffId(), shopId).ifPresent(existing -> {
            if (existing.isActive()) {
                throw new BusinessRuleException("Staff is already assigned to this shop");
            }
        });
        return staffShopRepository.save(StaffShop.assign(request.staffId(), shop));
    }

    /**
     * Unassigns a staff member from the shop identified by its ID based on the assignment ID.
     * Validates that the assignment exists and belongs to the specified shop before unassigning.
     */
    public void unassignStaff(Long shopId, Long assignmentId) {
        StaffShop assignment = staffShopRepository.findById(assignmentId)
                .filter(ss -> ss.getShop().getId().equals(shopId))
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        assignment.unassign();
    }

    // ----- INTERNAL HELPER METHODS -----

    private Shop loadShop(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + id));
    }
}
