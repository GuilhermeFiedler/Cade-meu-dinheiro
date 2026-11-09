package br.cademeudinheiro.config;

import br.cademeudinheiro.security.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor autenticacao;
    private final String origensPermitidas;

    public WebConfig(AuthInterceptor autenticacao, @Value("${app.cors-origens:}") String origensPermitidas) {
        this.autenticacao = autenticacao;
        this.origensPermitidas = origensPermitidas.trim();
    }

    /** Toda rota /api/** exige token, exceto o cadastro/login e a lista de endpoints. Rota nova nasce protegida. */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacao)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/auth/**", "/api/docs");
    }

    /** CORS só é habilitado se app.cors-origens (CORS_ORIGENS) listar os domínios do frontend, separados por vírgula. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (origensPermitidas.isEmpty()) {
            return;
        }
        registry.addMapping("/api/**")
            .allowedOrigins(origensPermitidas.split("\\s*,\\s*"))
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("Authorization", "Content-Type")
            .maxAge(3600);
    }
}
