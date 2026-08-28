# Próximos Passos e Problemas de Produção Detectados

Este documento detalha os problemas que você provavelmente enfrentará em seguida ao colocar a aplicação em produção (Vercel + Render) e como resolvê-los.

---

## 1. Bloqueio do Cookie de Refresh (`refresh_token`) no Navegador

### O Problema
Atualmente, no seu backend (Spring Boot), o cookie de refresh token está configurado da seguinte forma nos controllers de Login, Refresh e Logout:
* `.secure(false)`
* `.sameSite("Lax")`

Como o seu frontend está hospedado na Vercel (`vercel.app`) e o seu backend está no Render (`onrender.com`), qualquer chamada HTTP entre eles é considerada **Cross-Site (origens diferentes)**. 

Os navegadores modernos bloqueiam o salvamento de cookies Cross-Site se eles forem configurados como `Lax` ou se não possuírem a flag de segurança `Secure`.

### O Sintoma
O login inicial funcionará porque o `accessToken` é retornado no corpo da resposta JSON. No entanto, o cookie `refresh_token` **não será gravado pelo navegador**. Assim que a página for recarregada (F5) ou quando o token de acesso expirar, a renovação silenciosa (`silentRefresh`) falhará e o usuário será deslogado.

### A Solução
Você precisa ajustar o backend para usar configurações específicas para produção (usando HTTPS e SameSite None). 

Nos arquivos:
1. [`LoginController.java`](file:///c:/Users/winga/OneDrive/Documentos/code-projects/eloa_mercados_e_panificacoes/api/src/main/java/garcias/api/identity/authentication/infrastructure/presentation/controller/LoginController.java)
2. [`RefreshTokenController.java`](file:///c:/Users/winga/OneDrive/Documentos/code-projects/eloa_mercados_e_panificacoes/api/src/main/java/garcias/api/identity/authentication/infrastructure/presentation/controller/RefreshTokenController.java)
3. [`LogoutController.java`](file:///c:/Users/winga/OneDrive/Documentos/code-projects/eloa_mercados_e_panificacoes/api/src/main/java/garcias/api/identity/authentication/infrastructure/presentation/controller/LogoutController.java)

Altere a criação do `ResponseCookie` para:

```java
ResponseCookie refreshTokenCookie = ResponseCookie
        .from("refresh_token", tokenValue)
        .httpOnly(true)
        .secure(true)         // <--- TRUE: Obriga tráfego sob HTTPS (necessário para SameSite None)
        .sameSite("None")     // <--- NONE: Permite envio e salvamento em requisições Cross-Site (Vercel -> Render)
        .path("/api/auth")
        .maxAge(7 * 24 * 60 * 60)
        .build();
```

> [!TIP]
> Para não quebrar o desenvolvimento local (já que `localhost` geralmente roda sem HTTPS), você pode injetar o profile ativo do Spring Boot ou uma variável de ambiente e definir essas propriedades dinamicamente (ex: `.secure(isProduction)` e `.sameSite(isProduction ? "None" : "Lax")`).

---

## 2. Geração da Senha Randômica do Spring Security no Console

### O Problema
Nos logs do Render, você viu a mensagem:
`Using generated security password: be3ba627-867a-4fca-97a2-dde09476d45a`

O Spring Security gera isso automaticamente quando não encontra um bean do tipo `UserDetailsService` declarado na aplicação. Como a sua aplicação utiliza autenticação customizada e Stateless por meio de JWT, você não precisa da autoconfiguração de usuários em memória padrão do Spring.

### A Solução
Para remover essa mensagem confusa do console e economizar processamento desnecessário, você pode excluir a autoconfiguração de usuários no arquivo principal do backend:

No arquivo [`ApiApplication.java`](file:///c:/Users/winga/OneDrive/Documentos/code-projects/eloa_mercados_e_panificacoes/api/src/main/java/garcias/api/ApiApplication.java):

```java
package garcias.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
```
