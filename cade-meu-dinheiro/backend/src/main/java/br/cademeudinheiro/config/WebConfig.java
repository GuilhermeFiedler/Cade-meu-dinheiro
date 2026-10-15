package br.cademeudinheiro.config;

import br.cademeudinheiro.security.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor autenticacao;

    public WebConfig(AuthInterceptor autenticacao) {
        this.autenticacao = autenticacao;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacao)
            .addPathPatterns("/api/categorias", "/api/gastos", "/api/gastos/**", "/api/resumo");
    }
}
