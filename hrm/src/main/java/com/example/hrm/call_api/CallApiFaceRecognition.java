package com.example.hrm.call_api;

import com.example.hrm.dto.response.FaceRecognizeResponse;
import com.example.hrm.dto.response.MessageResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CallApiFaceRecognition {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String REGISTER_API  = "http://127.0.0.1:8000/facial-recognition/register-face";
    private static final String RECOGNIZE_API = "http://127.0.0.1:8000/facial-recognition/face-recognition";
    private static final String UPDATE_API    = "http://127.0.0.1:8000/facial-recognition/update-face";
    private static final String DELETE_API    = "http://127.0.0.1:8000/facial-recognition/delete-face";

    // ================= REGISTER =================
    public String registerFace(String employeeId, List<String> imagesBase64) {
        Map<String, Object> requestBody = Map.of(
                "employee_id", employeeId,
                "images", imagesBase64
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<MessageResponse> response =
                    restTemplate.postForEntity(REGISTER_API, request, MessageResponse.class);

            return Objects.requireNonNull(response.getBody()).getMessage();
        } catch (Exception e) {
            throw new RuntimeException("Gọi API register-face thất bại: " + e.getMessage());
        }
    }

    // ================= RECOGNIZE =================
    public FaceRecognizeResponse recognizeFace(String imageBase64) {
        Map<String, Object> requestBody = Map.of(
                "image", imageBase64
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<FaceRecognizeResponse> response =
                    restTemplate.postForEntity(RECOGNIZE_API, request, FaceRecognizeResponse.class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Gọi API face-recognition thất bại: " + e.getMessage());
        }
    }

    // ================= UPDATE =================
    public String updateFace(String employeeId, List<String> newImagesBase64) {
        Map<String, Object> requestBody = Map.of(
                "employee_id", employeeId,
                "new_images", newImagesBase64
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<MessageResponse> response =
                    restTemplate.exchange(
                            UPDATE_API,
                            HttpMethod.PUT,
                            request,
                            MessageResponse.class
                    );

            return Objects.requireNonNull(response.getBody()).getMessage();
        } catch (Exception e) {
            throw new RuntimeException("Gọi API update-face thất bại: " + e.getMessage());
        }
    }

    // ================= DELETE =================
    public String deleteFace(String employeeId) {
        Map<String, Object> requestBody = Map.of(
                "employee_id", employeeId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<MessageResponse> response =
                    restTemplate.exchange(
                            DELETE_API,
                            HttpMethod.DELETE,
                            request,
                            MessageResponse.class
                    );


            return Objects.requireNonNull(response.getBody()).getMessage();
        } catch (Exception e) {
            throw new RuntimeException("Gọi API delete-face thất bại: " + e.getMessage());
        }
    }
}
