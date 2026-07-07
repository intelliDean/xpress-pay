package com.api.xpress.auth_config.user.services;

import com.api.xpress.auth_config.user.data.models.XpressToken;
import com.api.xpress.auth_config.user.data.repositories.XpressTokenRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class XpressTokenServiceImpl implements XpressTokenService {

    private final XpressTokenRepository xpressTokenRepository;

    @Override
    public void saveToken(XpressToken xpressToken)   {
        xpressTokenRepository.save(xpressToken);
    }

    @Override		//this method retrieves a token object by either access or refresh token
    public Optional<XpressToken> getValidTokenByAnyToken(String anyToken) {
        return xpressTokenRepository.findValidTokenByToken(anyToken);
    }

    @Override		//this method revokes the token
    public void revokeToken(String accessToken) {
        getValidTokenByAnyToken(accessToken)
                .ifPresent(xpressToken -> {
                    xpressToken.setRevoked(true);
                    xpressTokenRepository.save(xpressToken);
                });
    }

    @Override		//this method checks if a token is revoked or not
    public boolean isTokenValid(String anyToken) {
        return getValidTokenByAnyToken(anyToken)
                .map(xpressToken -> !xpressToken.isRevoked())
                .orElse(false);
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Africa/Lagos") //schedule to run every midnight
    private void deleteAllRevokedTokens() {		//this is a cron task to delete all revoked or expired tokens from the database
        final List<XpressToken> allRevokedTokens =
                xpressTokenRepository.findAllInvalidTokens();
        if (!allRevokedTokens.isEmpty()) {
            xpressTokenRepository.deleteAll(allRevokedTokens);
        }
    }

    @Scheduled(cron = "0 0 */6 * * *", zone = "Africa/Lagos")   //scheduled to run every 6 hours daily
    private void setTokenExpiration() {
        //this checks the database for expired tokens and set it to true so cron task could pick them and delete
        final List<XpressToken> notExpiredTokens = xpressTokenRepository.findAllTokenNotExpired();

        notExpiredTokens.stream()
                .filter(
                        token -> token.getCreatedAt()
                                .plusDays(7)
                                .isBefore(LocalDateTime.now())
                )
                .forEach(token -> token.setExpired(true));
        xpressTokenRepository.saveAll(notExpiredTokens);
    }
}
