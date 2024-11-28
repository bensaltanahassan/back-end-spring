package com.exam_jee.ds.controllers;

import com.exam_jee.ds.model.Transaction;
import com.exam_jee.ds.model.User;
import com.exam_jee.ds.payload.request.RegisterBanquierRequest;
import com.exam_jee.ds.payload.request.SaveClient;
import com.exam_jee.ds.payload.request.TransferArgent;
import com.exam_jee.ds.payload.response.ApiResponse;
import com.exam_jee.ds.payload.response.ApiResponseWithData;
import com.exam_jee.ds.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
    public ResponseEntity<?> allUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(new ApiResponseWithData<>(true, "Get profile", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            return ResponseEntity.ok(new ApiResponseWithData<>(true, "Get profile", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @PostMapping("/client")
    public ResponseEntity<?> saveClient(
            @RequestBody SaveClient client
    ) {
        if (userService.existsByEmail(client.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false,"Error: Email is already in use!"));
        }
        try {
            userService.saveClient(client);
            return ResponseEntity.ok(new ApiResponse(true, "Client successfully created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @PutMapping("/{clientId}")
    public ResponseEntity<?> editProfile(@PathVariable Long clientId,@RequestBody SaveClient editRequest) {
        try {
            userService.editUserProfile(clientId,editRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Updated Profile"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }


    @PutMapping("/enable-client/{clientId}")
    public ResponseEntity<?> enableClient(@PathVariable Long clientId) {
        try {
            userService.enableClient(clientId);
            return ResponseEntity.ok(new ApiResponse(true, "Client enabled"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @GetMapping("/all-bons-clients")
    public ResponseEntity<?> getAllDeliveries() {
        try {
            List<User> deliveries = userService.getAllDeliveries();
            return ResponseEntity.ok(new ApiResponseWithData<>(true, "Get All Delivery", deliveries));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @GetMapping("/all-clients")
    public ResponseEntity<?> getAllClients() {
        try {
            List<User> clients = userService.getAllClients();
            return ResponseEntity.ok(new ApiResponseWithData<>(true, "Get All Clients", clients));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @PostMapping("/transfer")
    public ResponseEntity<String> transferArgent(@RequestBody TransferArgent transferArgent) {

        try {
            userService.transferArgent(transferArgent);
            return ResponseEntity.ok("Transfert d'argent réussi !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du transfert d'argent.");
        }
    }

    @GetMapping("/historique/{clientId}")
    public List<Transaction> getHistoriqueTransactions(@PathVariable Long clientId) {
        return userService.getHistoriqueTransactions(clientId);
    }
    @GetMapping("/operations")
    public List<Transaction> getAllTransactions() {
        return userService.getAllTransactions();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUSer(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse(true, "user deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
