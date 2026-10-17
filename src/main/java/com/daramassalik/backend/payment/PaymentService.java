package com.daramassalik.backend.payment;
import com.daramassalik.backend.common.ResourceNotFoundException; import com.daramassalik.backend.student.StudentRepository;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Service public class PaymentService {
 private final PaymentRepository repository; private final StudentRepository studentRepository;
 public PaymentService(PaymentRepository repository, StudentRepository studentRepository){this.repository=repository;this.studentRepository=studentRepository;}
 @Transactional public PaymentResponse create(PaymentRequest request){var student=studentRepository.findById(request.studentId()).orElseThrow(()->new ResourceNotFoundException("Student not found: "+request.studentId())); var p=new Payment(); p.setStudent(student);p.setMonth(request.month());p.setYear(request.year());p.setAmount(request.amount());p.setPaymentDate(request.paymentDate());p.setComment(request.comment());return toResponse(repository.save(p));}
 @Transactional(readOnly=true) public List<PaymentResponse> findByMonth(int year,int month){return repository.findByYearAndMonth(year,month).stream().map(this::toResponse).toList();}
 @Transactional(readOnly=true) public List<PaymentResponse> findStudentHistory(Long studentId){return repository.findByStudentIdOrderByYearDescMonthDesc(studentId).stream().map(this::toResponse).toList();}
 private PaymentResponse toResponse(Payment p){return new PaymentResponse(p.getId(),p.getStudent().getId(),p.getStudent().getFirstName()+" "+p.getStudent().getLastName(),p.getMonth(),p.getYear(),p.getAmount(),p.getPaymentDate(),p.getComment());}
}
