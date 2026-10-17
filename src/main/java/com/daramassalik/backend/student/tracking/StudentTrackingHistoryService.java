package com.daramassalik.backend.student.tracking;

import com.daramassalik.backend.attendance.AttendanceRepository;
import com.daramassalik.backend.attendance.AttendanceResponse;
import com.daramassalik.backend.attendance.AttendanceService;
import com.daramassalik.backend.common.ResourceNotFoundException;
import com.daramassalik.backend.pedagogy.PedagogicalTrackingRepository;
import com.daramassalik.backend.pedagogy.PedagogicalTrackingResponse;
import com.daramassalik.backend.pedagogy.PedagogicalTrackingService;
import com.daramassalik.backend.student.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudentTrackingHistoryService {

    private final StudentRepository studentRepository;
    private final AttendanceService attendanceService;
    private final PedagogicalTrackingService pedagogicalTrackingService;

    public StudentTrackingHistoryService(
            StudentRepository studentRepository,
            AttendanceService attendanceService,
            PedagogicalTrackingService pedagogicalTrackingService) {
        this.studentRepository = studentRepository;
        this.attendanceService = attendanceService;
        this.pedagogicalTrackingService = pedagogicalTrackingService;
    }

    @Transactional(readOnly = true)
    public StudentTrackingHistoryResponse getHistory(Long studentId) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found: " + studentId));

        List<AttendanceResponse> attendances =
                attendanceService.findStudentHistory(studentId);

        List<PedagogicalTrackingResponse> trackings =
                pedagogicalTrackingService.findStudentHistory(studentId);

        Map<java.time.LocalDate, AttendanceResponse> attendanceByDate =
                attendances.stream().collect(Collectors.toMap(
                        AttendanceResponse::date,
                        Function.identity(),
                        (first, second) -> first));

        Map<java.time.LocalDate, PedagogicalTrackingResponse> trackingByDate =
                trackings.stream().collect(Collectors.toMap(
                        PedagogicalTrackingResponse::date,
                        Function.identity(),
                        (first, second) -> first));

        var dates = new TreeSet<java.time.LocalDate>(
                Comparator.reverseOrder());
        dates.addAll(attendanceByDate.keySet());
        dates.addAll(trackingByDate.keySet());

        List<StudentTrackingHistoryEntry> history = dates.stream()
                .map(date -> new StudentTrackingHistoryEntry(
                        date,
                        attendanceByDate.get(date),
                        trackingByDate.get(date)))
                .toList();

        return new StudentTrackingHistoryResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                history);
    }
}