package br.senai.arenaxp.security;

import br.senai.arenaxp.model.Usuario;
import br.senai.arenaxp.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Carrega o usuário da tabela "usuarios" usando o e-mail como login. */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getPerfil().toUpperCase())
                .build();
    }
}
