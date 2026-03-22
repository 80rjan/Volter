package com.volter.backend.staff;

import com.volter.backend.common.utils.Validate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffService {

    protected final StaffRepository staffRepository;
    protected final Validate validate;

    public Staff getById(Long id) {
        return staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Staff with ID " + id + " not found!"));
    }

}
