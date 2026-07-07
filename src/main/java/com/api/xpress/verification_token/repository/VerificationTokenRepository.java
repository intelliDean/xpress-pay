package com.api.xpress.verification_token.repository;

import com.api.xpress.verification_token.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @Query("""
            select token from VerificationToken token
            where token.token = :token and token.email = :email and token.revoked = false
            """)
    Optional<VerificationToken> findValidVerificationByTokenAndEmail(String token, String email);

    @Query("""
            select tokens from VerificationToken tokens
            where tokens.revoked = true or tokens.expired = true
            """)
    List<VerificationToken> findAllRevokedTokens();

    @Query("""
              select token from VerificationToken token
            where token.revoked = false and token.expired = false
            """)
    List<VerificationToken> findAllValidTokens();
}
