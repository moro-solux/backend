package com.example.moro.app.sample;

import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sample")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponseTemplate<String>> hello() {
        String message = sampleService.getHelloMessage();
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, message);
    }

    @PostMapping("/echo")
    public ResponseEntity<ApiResponseTemplate<String>> echo(@RequestBody SampleRequestDTO request) {
        String message = sampleService.echoMessage(request);
        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, message);
    }
}
