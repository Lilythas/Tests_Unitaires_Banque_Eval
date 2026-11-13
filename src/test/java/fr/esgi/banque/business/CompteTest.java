package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

/**
 * Test d'exemple fourni pour vérifier que le projet est bien configuré.
 * À compléter (partie A).
 */
class CompteTest {
	// Crée un compte valide
	private Compte compte(String solde, String decouvert) {
		return new Compte("FR01", "Alice", new BigDecimal(solde), new BigDecimal(decouvert));
	}
	@Test
	void shouldIncreaseBalanceWhenCrediting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act
		compte.crediter(new BigDecimal("50.00"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("150.00");
	}
	// ===== C1 =====
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"   "})
	void C1_numero_invalide_refuse(String numero) {
		// Arrange
		BigDecimal solde = new BigDecimal("100");
		BigDecimal decouvert = new BigDecimal("200");
		// Act + Assert
		assertThatThrownBy(() -> new Compte(numero, "Alice", solde, decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"   "})
	void C1_titulaire_invalide_refuse(String titulaire) {
		BigDecimal solde = new BigDecimal("100");
		BigDecimal decouvert = new BigDecimal("200");
		assertThatThrownBy(() -> new Compte("FR01", titulaire, solde, decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void C1_numero_et_titulaire_valides_acceptes() {
		assertThatCode(() -> compte("100", "200")).doesNotThrowAnyException();
	}

	// ===== C2 =====
	@Test
	void C2_solde_initial_null_refuse() {
		assertThatThrownBy(() -> new Compte("FR01", "Alice", null, new BigDecimal("200")))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void C2_decouvert_null_refuse() {
		assertThatThrownBy(() -> new Compte("FR01", "Alice", new BigDecimal("100"), null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ValueSource(strings = {"-0.01", "-1", "-200"})
	void C2_decouvert_negatif_refuse(String decouvert) {
		assertThatThrownBy(() -> compte("100", decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "0.00", "200"})
	void C2_decouvert_positif_ou_nul_accepte(String decouvert) {
		assertThatCode(() -> compte("100", decouvert)).doesNotThrowAnyException();
	}

	// ===== C3 =====
	@ParameterizedTest
	@ValueSource(strings = {"0", "0.00", "-0.01", "-100"})
	void C3_crediter_montant_non_positif_refuse(String montant) {
		Compte c = compte("100", "200");
		assertThatThrownBy(() -> c.crediter(new BigDecimal(montant)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void C3_crediter_null_refuse() {
		Compte c = compte("100", "200");
		assertThatThrownBy(() -> c.crediter(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({
			"0.01, 100.01",
			"50,   150"
	})
	void C3_crediter_montant_positif_augmente_solde(String montant, String soldeAttendu) {
		// Arrange
		Compte c = compte("100", "200");
		// Act
		c.crediter(new BigDecimal(montant));
		// Assert
		assertThat(c.getSolde()).isEqualByComparingTo(soldeAttendu);
	}

	// ===== C4 =====
	@ParameterizedTest
	@ValueSource(strings = {"0", "0.00", "-0.01", "-100"})
	void C4_debiter_montant_non_positif_refuse(String montant) {
		Compte c = compte("100", "200");
		assertThatThrownBy(() -> c.debiter(new BigDecimal(montant)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void C4_debiter_null_refuse() {
		Compte c = compte("100", "200");
		assertThatThrownBy(() -> c.debiter(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void C4_debiter_montant_positif_diminue_solde() {
		Compte c = compte("100", "200");
		c.debiter(new BigDecimal("0.01"));
		assertThat(c.getSolde()).isEqualByComparingTo("99.99");
	}

	// ===== C5 =====
	@ParameterizedTest
	@CsvSource({
			"100, 200, 300,    -200",     // limite exacte : acceptée
			"100, 200, 299.99, -199.99",  // juste avant la limite
			"100, 0,   100,    0"         // sans découvert, on peut aller à 0
	})
	void C5_debit_jusqu_a_la_limite_accepte(String solde, String decouvert,
											String montant, String soldeAttendu) {
		// Arrange
		Compte c = compte(solde, decouvert);
		// Act
		c.debiter(new BigDecimal(montant));
		// Assert
		assertThat(c.getSolde()).isEqualByComparingTo(soldeAttendu);
	}

	@ParameterizedTest
	@CsvSource({
			"100, 200, 300.01",   // juste au-delà de la limite
			"100, 0,   100.01",
			"0,   0,   0.01"
	})
	void C5_debit_au_dela_refuse_et_solde_inchange(String solde, String decouvert, String montant) {
		// Arrange
		Compte c = compte(solde, decouvert);
		// Act + Assert
		assertThatThrownBy(() -> c.debiter(new BigDecimal(montant)))
				.isInstanceOf(SoldeInsuffisantException.class);
		assertThat(c.getSolde()).isEqualByComparingTo(solde);  // le solde n'a pas bougé
	}

	// ===== C6 =====
	@ParameterizedTest
	@ValueSource(strings = {"0", "0.00", "0.0", "0.000"})
	void C6_estVide_vrai_quelle_que_soit_l_ecriture_de_zero(String zero) {
		Compte c = compte(zero, "0");
		assertThat(c.estVide()).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {"0.01", "100"})
	void C6_estVide_faux_si_solde_non_nul(String solde) {
		Compte c = compte(solde, "0");
		assertThat(c.estVide()).isFalse();
	}

	// ===== C7 =====
	@ParameterizedTest
	@CsvSource({
			"100, 100.01, true",    // solde -0.01 : à découvert
			"100, 100,    false",   // solde 0 : pas à découvert
			"100, 99.99,  false"    // solde 0.01
	})
	void C7_estADecouvert_si_solde_strictement_negatif(String solde, String debit, boolean attendu) {
		// Arrange
		Compte c = compte(solde, "200");
		// Act
		c.debiter(new BigDecimal(debit));
		// Assert
		assertThat(c.estADecouvert()).isEqualTo(attendu);
	}

}
