package com.troubleticket.trouble_ticket_api.infrastructure.config;

import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.application.service.TroubleTicketService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    TroubleTicketService troubleTicketService(
            TroubleTicketRepository repository
    ) {
        return new TroubleTicketService(repository);
    }

    @Bean
    CreateTroubleTicketUseCase createTroubleTicketUseCase(
            TroubleTicketService service
    ) {
        return service;
    }

    @Bean
    GetTroubleTicketUseCase getTroubleTicketUseCase(
            TroubleTicketService service
    ) {
        return service;
    }
}