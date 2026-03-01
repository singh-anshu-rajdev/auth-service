package com.smartBankElite.authserver.Controller;

import com.smartBankElite.authserver.DTO.CacheDTO;
import com.smartBankElite.authserver.ServiceImpl.JwtServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private JwtServiceImpl jwtService;

    /**
     * GET /api/cache
     * Accepts a JWT either via Authorization: Bearer <token> header or ?token=<token> query param
     * Returns the CacheDTO parsed from the token.
     */
    @GetMapping("/cache")
    public ResponseEntity<?> getCacheDTO(HttpServletRequest httpServletRequest) {
        try {
            CacheDTO cacheDTO = jwtService.getCacheDTOFromToken(httpServletRequest);
            return ResponseEntity.ok(cacheDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token: " + e.getMessage());
        }
    }

}
