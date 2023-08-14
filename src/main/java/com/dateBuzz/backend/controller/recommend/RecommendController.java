package com.dateBuzz.backend.controller.recommend;

import com.dateBuzz.backend.controller.responseDto.RecordResponseDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.exception.DateBuzzException;
import com.dateBuzz.backend.model.entity.UserEntity;
import com.dateBuzz.backend.repository.UserRepository;
import com.dateBuzz.backend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import static com.dateBuzz.backend.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecommendController {

    private final RecommendService recommendService;

    private final UserRepository userRepository;

    @GetMapping("/recommendation")
    public Response<List<RecordResponseDto>> getRecommendation(Authentication authentication) {
        UserEntity user = userRepository
                .findByUserName(authentication.getName())
                .orElseThrow(() -> new DateBuzzException(USER_NOT_FOUND, String.format("%s is not found", authentication.getName())));

        String currentPath = System.getProperty("user.dir");
        String scriptPath = currentPath + "/python/main.py";

        return Response.success(recommendService.getRecommendation(callPythonScript(scriptPath, String.valueOf(user.getId()))));
    }


    public String callPythonScript(String scriptPath, String userId) {
        StringBuilder result = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptPath, userId);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            while ((line = stderrReader.readLine()) != null) {
                System.err.println("Error: " + line);
                log.error(line);
            }
            reader.close();
            stderrReader.close();
        } catch (Exception e) {
            throw new DateBuzzException(PYTHON_READING_PROBLEM);
        }
        return result.toString();
    }
}
