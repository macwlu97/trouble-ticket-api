package com.troubleticket.trouble_ticket_api.infrastructure.config;

import com.troubleticket.trouble_ticket_api.application.port.in.AddNoteUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CloseTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.application.service.TroubleTicketService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public TroubleTicketService troubleTicketService(
            TroubleTicketRepository repository
    ) {
        return new TroubleTicketService(repository);
    }

    @Bean
    public CreateTroubleTicketUseCase createTroubleTicketUseCase(
            @Qualifier("troubleTicketService") TroubleTicketService service
    ) {
        return service;
    }

    @Bean
    public GetTroubleTicketUseCase getTroubleTicketUseCase(
            @Qualifier("troubleTicketService") TroubleTicketService service
    ) {
        return service;
    }

    @Bean
    public CloseTroubleTicketUseCase closeTroubleTicketUseCase(
            @Qualifier("troubleTicketService") TroubleTicketService service
    ) {
        return service;
    }

    @Bean
    public AddNoteUseCase addNoteUseCase(
            @Qualifier("troubleTicketService") TroubleTicketService service
    ) {
        return service;
    }
}