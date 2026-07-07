package com.api.xpress.verification_token.service;

import com.api.xpress.verification_token.repository.VerificationTokenRepository;
import com.api.xpress.verification_token.model.VerificationToken;
import com.api.xpress.xceptions.XpressException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public void saveToken(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public boolean isValid(VerificationToken verificationToken) {
        return verificationToken != null &&
                verificationToken.getExpireAt()
                        .isAfter(LocalDateTime.now());
    }

    @Override
    public VerificationToken findByTokenAndEmail(String token, String email) {
        return verificationTokenRepository.findValidVerificationByTokenAndEmail(token, email)
                .orElseThrow(()-> new XpressException("Token could not be found"));
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Africa/Lagos") //scheduled to run every midnight
    private void deleteAllRevokedTokens() {
        final List<VerificationToken> allRevokedTokens =
                verificationTokenRepository.findAllRevokedTokens();
        if (!allRevokedTokens.isEmpty()) {
            verificationTokenRepository.deleteAll(allRevokedTokens);
        }
    }

    @Scheduled(cron = "0 0 * * * ?", zone = "Africa/Lagos")
    private void setExpiredToken() {
        final List<VerificationToken> tokens = verificationTokenRepository.findAllValidTokens();
        tokens.stream().filter(
                token -> token.getExpireAt()
                        .isBefore(LocalDateTime.now())
        ).forEach(init -> init.setExpired(true));
        verificationTokenRepository.saveAll(tokens);
    }
}

