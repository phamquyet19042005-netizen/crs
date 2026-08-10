package vn.edu.crs.registrationservice.service;

import vn.edu.crs.registrationservice.client.CourseClient;
import vn.edu.crs.registrationservice.dto.RegistrationRequestDTO;
import vn.edu.crs.registrationservice.entity.Registration;
import vn.edu.crs.registrationservice.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private static final String DA_DANG_KY = "DA_DANG_KY";
    private static final String DA_HUY = "DA_HUY";

    private final RegistrationRepository registrationRepository;
    private final CourseClient courseClient;

    public Registration register(RegistrationRequestDTO dto) {
        if (registrationRepository.existsByStudentIdAndCourseIdAndTrangThai(
                dto.getStudentId(), dto.getCourseId(), DA_DANG_KY)) {
            throw new IllegalStateException("Sinh vien da dang ky mon hoc nay roi");
        }

        // Step 1: call course-service to reserve seat FIRST
        courseClient.reserveSeat(dto.getCourseId());

        // Step 2: save Registration ONLY AFTER course-service confirms successfully
        Registration registration = new Registration();
        registration.setStudentId(dto.getStudentId());
        registration.setCourseId(dto.getCourseId());
        registration.setTrangThai(DA_DANG_KY);
        registration.setNgayDangKy(LocalDateTime.now());

        return registrationRepository.save(registration);
    }

    public void cancel(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay dang ky id = " + registrationId));

        if (DA_HUY.equals(registration.getTrangThai())) {
            throw new IllegalStateException("Dang ky nay da duoc huy truoc do");
        }

        // Call course-service to release seat FIRST before changing status
        courseClient.releaseSeat(registration.getCourseId());

        registration.setTrangThai(DA_HUY);
        registrationRepository.save(registration);
    }
}
