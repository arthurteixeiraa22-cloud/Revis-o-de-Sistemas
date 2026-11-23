package br.senai.arenaxp.controller;

import br.senai.arenaxp.model.Categorias;
import br.senai.arenaxp.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/** Atributos disponíveis em todas as telas (nome do usuário no cabeçalho, categorias). */
@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioRepository usuarioRepository;

    public GlobalModelAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("nomeUsuario")
    public String nomeUsuario(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return usuarioRepository.findByEmailIgnoreCase(auth.getName())
                .map(u -> u.getNome())
                .orElse(auth.getName());
    }

    @ModelAttribute("categorias")
    public List<String> categorias() {
        return Categorias.TODAS;
    }
}
