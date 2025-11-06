package com.project.code.Controller;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;

    public ReviewController(ReviewRepository reviewRepository, CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/{storeId}/{productId}")
    public ResponseEntity<Map<String, Object>> getReviews(@PathVariable Long storeId, @PathVariable Long productId) {
        Map<String, Object> resp = new HashMap<>();
        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);
        List<Map<String, Object>> out = new ArrayList<>();
        if (reviews != null) {
            for (Review r : reviews) {
                Map<String, Object> item = new HashMap<>();
                item.put("comment", r.getComment());
                item.put("rating", r.getRating());
                Customer c = null;
                if (r.getCustomerId() != null) {
                    c = customerRepository.findById(r.getCustomerId()).orElse(null);
                }
                item.put("customerName", c != null ? c.getName() : null);
                out.add(item);
            }
        }
        resp.put("reviews", out);
        return ResponseEntity.ok(resp);
    }

}
