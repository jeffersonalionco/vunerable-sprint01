package br.unipar.frameworks.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt pra gravar senha no banco, 
 * nunca mais plain text.
 * O Spring Security usa esse bean no login (matches).
 */

@Service
public class ServicoHashSenha implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    /**
     * Chama isso antes de salvar User no register ou no seed.
     */

    
    public String criptografarSenha(String senhaPlana) {
        return bcrypt.encode(senhaPlana);
    }

    @Override
    public String encode(CharSequence senhaPlana) {
        return bcrypt.encode(senhaPlana);
    }

    @Override
    public boolean matches(CharSequence senhaPlana, String hashArmazenado) {
        return bcrypt.matches(senhaPlana, hashArmazenado);
    }
}
