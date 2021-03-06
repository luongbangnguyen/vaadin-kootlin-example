package com.vaadin.example.api.config.oauth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.approval.ApprovalStore
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory
import org.springframework.security.oauth2.provider.token.TokenStore


@Configuration
@EnableAuthorizationServer
open class AuthorizationServerConfig : AuthorizationServerConfigurerAdapter() {

    @Autowired
    @Qualifier("clientDetailsServiceImpl")
    private lateinit var clientDetailsService: ClientDetailsService

    @Autowired
    @Qualifier("tokenStoreImp")
    private lateinit var tokenStore: TokenStore

    @Autowired
    private lateinit var approvalStore: ApprovalStore

    @Autowired
    private lateinit var userApprovalHandler: UserApprovalHandler

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.userDetailsService(this.userDetailsService).tokenStore(tokenStore)
                .authenticationManager(authenticationManager).userApprovalHandler(userApprovalHandler)
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.withClientDetails(this.clientDetailsService)
    }

    @Bean
    open fun userApprovalHandler(): UserApprovalHandler {
        val handler = ApprovalStoreUserApprovalHandler()
        handler.setApprovalStore(approvalStore)
        handler.setRequestFactory(DefaultOAuth2RequestFactory(clientDetailsService))
        handler.setClientDetailsService(clientDetailsService)
        return handler
    }

    @Bean
    open fun approvalStore(): ApprovalStore {
        val store = TokenApprovalStore()
        store.setTokenStore(tokenStore)
        return store
    }
}