package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Compte;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirementServiceImplTest {

    @Mock
    CompteRepository repository;

    @Mock
    NotificationService notifications;

    // Vendredi 13 novembre 2026
    Clock vendredi = Clock.fixed(Instant.parse("2026-11-13T10:00:00Z"), ZoneId.of("Europe/Paris"));

    VirementServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VirementServiceImpl(repository, notifications, vendredi);
    }

    // Méthode utilitaire : crée un compte sans découvert
    private Compte compte(String numero, String titulaire, String solde) {
        return new Compte(numero, titulaire, new BigDecimal(solde), BigDecimal.ZERO);
    }

    // ===== V1 =====
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "0.00", "-0.01", "-100"})
    void V1_montant_null_nul_ou_negatif_refuse(BigDecimal montant) {
        assertThatThrownBy(() -> service.virer("A", "B", montant))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ===== V2 =====
    @Test
    void V2_virement_vers_le_meme_compte_refuse() {
        assertThatThrownBy(() -> service.virer("A", "A", new BigDecimal("100")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}