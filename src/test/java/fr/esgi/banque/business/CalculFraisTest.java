package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

class CalculFraisTest {

    private final CalculFrais calcul = new CalculFrais();

    // ===== F1 =====
    @ParameterizedTest
    @ValueSource(ints = {17, 0, -1})
    void F1_moins_de_18_ans_refuse(int age) {
        assertThatThrownBy(() -> calcul.fraisMensuels(age, new BigDecimal("500"), false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void F1_18_ans_accepte() {
        assertThatCode(() -> calcul.fraisMensuels(18, new BigDecimal("500"), false))
                .doesNotThrowAnyException();
    }

    @Test
    void F1_solde_moyen_null_refuse() {
        assertThatThrownBy(() -> calcul.fraisMensuels(30, null, false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ===== F2 =====
    @Test
    void F2_frais_de_tenue_2_euros() {
        // Arrange : 30 ans, solde 500, pas premium -> aucune exonération
        // Act
        BigDecimal frais = calcul.fraisMensuels(30, new BigDecimal("500"), false);
        // Assert
        assertThat(frais).isEqualByComparingTo("2.00");
    }

    // ===== F3 =====
    @ParameterizedTest
    @CsvSource({
            "18, 0.00",
            "25, 0.00",   // limite : 25 ans -> offert
            "26, 2.00",   // limite : 26 ans -> payant
            "40, 2.00"
    })
    void F3_tenue_offerte_avant_26_ans(int age, String fraisAttendus) {
        BigDecimal frais = calcul.fraisMensuels(age, new BigDecimal("500"), false);
        assertThat(frais).isEqualByComparingTo(fraisAttendus);
    }

    // ===== F4 =====
    @ParameterizedTest
    @CsvSource({
            "999.99,  2.00",  // juste sous la limite
            "1000,    0.00",  // limite exacte -> offert
            "1000.00, 0.00",
            "5000,    0.00"
    })
    void F4_tenue_offerte_si_solde_moyen_au_moins_1000(String solde, String fraisAttendus) {
        BigDecimal frais = calcul.fraisMensuels(30, new BigDecimal(solde), false);
        assertThat(frais).isEqualByComparingTo(fraisAttendus);
    }

    // ===== F5 =====
    @ParameterizedTest
    @CsvSource({
            "30, 500,  7.00",   // tenue payante + premium
            "20, 500,  5.00",   // tenue offerte (âge) + premium
            "30, 1000, 5.00",   // tenue offerte (solde) + premium
            "20, 1000, 5.00"    // les deux exonérations + premium
    })
    void F5_carte_premium_toujours_due(int age, String solde, String fraisAttendus) {
        BigDecimal frais = calcul.fraisMensuels(age, new BigDecimal(solde), true);
        assertThat(frais).isEqualByComparingTo(fraisAttendus);
    }
}