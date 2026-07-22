package org.ats.features.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ats.features.auth.dto.CustomUserDetails;
import org.ats.features.auth.dto.TokenRefreshResult;
import org.ats.features.auth.dto.UserProfile;
import org.ats.features.auth.dto.UserRequest;
import org.ats.features.auth.service.RefreshTokenService;
import org.ats.security.JwtAuthenticationFilter;
import org.ats.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auths")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    // The refresh cookie is scoped to the auth endpoints only, so it is never sent on normal API calls.
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String REFRESH_COOKIE_PATH = "/api/v1/auths";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @Value("${expiration-time}")
    private long accessExpirationMs;

    @Value("${refresh-expiration-time}")
    private long refreshExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody UserRequest userRequest) {
        log.info("Login attempt for email={}", userRequest.getEmail());

        try {
            // Delegate to AuthenticationManager (verifies password via DaoAuthenticationProvider)
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Issue a short-lived access token and a long-lived, rotating refresh token,
            // each in its own httpOnly cookie (not readable by JS -> safe from XSS).
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = refreshTokenService.createToken(userDetails.getUsername());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie(JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE, accessToken, Duration.ofMillis(accessExpirationMs)).toString())
                    .header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(REFRESH_TOKEN_COOKIE, refreshToken, Duration.ofMillis(refreshExpirationMs)).toString())
                    .body(toProfile(userDetails));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for email={}: {}", userRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Exchanges a valid refresh token for a new access token.
     * The refresh token is rotated: the old one is revoked and a new one is set.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {

        log.info("Refresh token attempt");

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TokenRefreshResult result = refreshTokenService.rotate(refreshToken);

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(result.email());
            String newAccessToken = jwtService.generateAccessToken(userDetails);


            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie(JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE, newAccessToken, Duration.ofMillis(accessExpirationMs)).toString())
                    .header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(REFRESH_TOKEN_COOKIE, result.refreshToken(), Duration.ofMillis(refreshExpirationMs)).toString())
                    .body(toProfile(userDetails));
        } catch (AuthenticationException e) {
            // Refresh token invalid/expired/revoked -> clear both cookies so the FE logs out cleanly.
            log.warn("Refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie(JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE, "", Duration.ZERO).toString())
                    .header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(REFRESH_TOKEN_COOKIE, "", Duration.ZERO).toString())
                    .build();
        }
    }

    /**
     * Returns the currently authenticated user's profile.
     * Used by the FE on app load to restore the session from the httpOnly cookie.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfile> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(toProfile(userDetails));
    }

    /**
     * Revokes the refresh token server-side and clears both cookies.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        refreshTokenService.revoke(refreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie(JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE, "", Duration.ZERO).toString())
                .header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(REFRESH_TOKEN_COOKIE, "", Duration.ZERO).toString())
                .build();
    }

    private ResponseCookie buildAccessTokenCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // TODO: set true in production (HTTPS) and use SameSite=None for cross-site
                .sameSite("Lax")
                .maxAge(maxAge)
                .path("/")
                .build();
    }

    private ResponseCookie buildRefreshTokenCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // TODO: set true in production (HTTPS)
                .sameSite("Lax")
                .maxAge(maxAge)
                .path(REFRESH_COOKIE_PATH)
                .build();
    }

    private UserProfile toProfile(CustomUserDetails userDetails) {
        return UserProfile.builder()
                .email(userDetails.getUsername())
                .fullName(userDetails.getFullName())
                .roles(userDetails.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .toList())
                .build();
    }
}
