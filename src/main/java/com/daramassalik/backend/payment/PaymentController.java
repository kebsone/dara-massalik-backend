package com.daramassalik.backend.payment;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/payments") @Tag(name="Payments") @SecurityRequirement(name="bearerAuth") public class PaymentController {
 private final PaymentService service; public PaymentController(PaymentService service){this.service=service;}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public PaymentResponse create(@Valid @RequestBody PaymentRequest request){return service.create(request);}
 @GetMapping public List<PaymentResponse> findByMonth(@RequestParam int year,@RequestParam int month){return service.findByMonth(year,month);}
 @GetMapping("/student/{studentId}") public List<PaymentResponse> findStudentHistory(@PathVariable Long studentId){return service.findStudentHistory(studentId);}
}
