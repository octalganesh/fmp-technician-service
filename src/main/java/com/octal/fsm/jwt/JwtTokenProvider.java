package com.octal.fsm.jwt;

import com.octal.fsm.dto.AuthenticationResponse;
import com.octal.fsm.entities.Technician;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;

/**
 * @author naveen.kumawat
 * JwtTokenProvider - provides functionality to create JWT access and refresh token
 */
@Component
@RefreshScope
public class JwtTokenProvider {

    private static final Logger LOGGER = LogManager.getLogger(JwtTokenProvider.class);
    private static final String JWT_TOKEN_TYPE = "Bearer ";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String JWT_SCOPE = "scopes";
    @Value(value = "${jwt.access.token.validity}")
    public long jwtTokenValidity;
    @Value(value = "${jwt.refresh.token.validity}")
    public long jwtRefreshTokenValidity;
    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    /**
     * this method is used to create JWT token,
     * while generating token we pass scope and other user related information
     *
     * @param user - registered user object
     * @return - jwt token
     */
    public String createAccessJwtToken(Technician technician) {
        if (technician.getEmail() == null || technician.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Cannot create token without username");
        }

        Claims claims = Jwts.claims().setSubject(technician.getEmail());

        // Assuming Technician has a "role" field
        List<String> roles = new ArrayList<>();
//        if (technician.getRole() != null) {
//            roles.add(technician.getRole().getName());
//        } else {
            roles.add("ROLE_TECHNICIAN");
//        }

        claims.put("id", technician.getUuid());
        claims.put("emailId", technician.getEmail());
        claims.put("firstName", technician.getName());
        claims.put("lastName", technician.getName());
        claims.put("role", roles.get(0));
        claims.put("profileImage", technician.getProfilePicture());
        claims.put("permission", "");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(technician.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * generate new access token and refresh token when user login
     *
     * @param userDetails - authenticated user object
     * @return - jwt tokens and scopes
     */
    public AuthenticationResponse generateToken(Technician userDetails) {
        String access = createAccessJwtToken(userDetails);
        String refresh = createRefreshToken(userDetails, 1000);
        AuthenticationResponse response = new AuthenticationResponse();
        response.setJwtToken(access);
        response.setTokenType(JWT_TOKEN_TYPE);
        response.setRefreshToken(refresh);
        response.setEmail(userDetails.getEmail());
//        response.setAdmin(userDetails.getIsAdmin());
//        response.setRoleId(userDetails.getRole().getUuid());
        response.setProfileImage(userDetails.getProfilePicture());
        response.setUserName(userDetails.getName());

        return response;

    }


    /**
     * generate refresh token
     *
     * @param user       - user details object that contains user information
     * @param miliSecond
     * @return - returns access token
     */
    public String createRefreshToken(Technician user, int miliSecond) {

        if (user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Cannot create token without username");
        }
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        // set refresh token in scope claim
//        List<String> roles = new ArrayList<>(Collections.singleton(user.getRole().getName()));

        claims.put(JWT_SCOPE, Collections.singletonList(REFRESH_TOKEN));
        claims.put("id", user.getUuid());
        claims.put("emailId", user.getEmail());
        claims.put("firstName", user.getName());
//        claims.put("lastName", user.getLastName());
//        claims.put("role", roles.get(0));
        claims.put("profileImage", user.getProfilePicture());
        claims.put("permission", "");
        return Jwts.builder().setClaims(claims).setIssuer(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshTokenValidity * miliSecond))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }


    /**
     * validate refresh token that refresh has expired or not and check that jwt
     * token contains refresh scope or not
     *
     * @param token - JWT token
     * @return - true if refresh token is valid
     */
//    public boolean validateRefreshToken(String token) {
//        LOGGER.info("validating refresh token");
//
//        try {
//            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
//            List<String> scopes = claims.get(JWT_SCOPE, List.class);
//            boolean isTokenBeRefreshed = canTokenBeRefreshed(token);
//            if (!isTokenBeRefreshed) {
//                throw new ActorPayException("refresh.token.is.expired");
//            }
//            if (scopes == null || scopes.isEmpty() || scopes.stream().noneMatch(REFRESH_TOKEN::equals)) {
//                throw new AccessDeniedException("invalid.refresh.token");
//            }
//
//        } catch (ExpiredJwtException exception) {
//            throw new ActorPayException("refresh.token.is.expired");
//        }
//
//        return true;
//    }

    /**
     * check token can be refreshed or not
     *
     * @param token - JWT token
     * @return - return true if token can be refresh else false
     */
    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token));
    }

    /**
     * check JWT token has expired or not
     *
     * @param token - JWT token
     * @return - return true if jwt token has expired else false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * get expiration date from jwt token
     *
     * @param token - jwt token
     * @return - expiration date
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * get claims from jwt token
     *
     * @param token          - jwt token
     * @param claimsResolver - claim resolveer
     * @return - jwt claims
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * get all claims of jwt token using jwt secret
     *
     * @param token - jwt token
     * @return - jwt claims
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * get user name from jwt token
     *
     * @param token - jwt token
     * @return - username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * generate access token from refresh token
     *
     * @param user =
     * @return
     */
    public AuthenticationResponse generateTokenFromRefreshToken(Technician user) {
        LOGGER.debug("get new access token from refresh token");

        if (user != null) {
            String access = createAccessJwtToken(user);
            AuthenticationResponse response = new AuthenticationResponse();
            response.setJwtToken(access);
            response.setRefreshToken(createRefreshToken(user, 2000));
            response.setTokenType(JWT_TOKEN_TYPE);
            return response;
        }
        return null;
    }
}