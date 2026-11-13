# Rendu – Projet tests unitaires et TDD

**Nom :** CHARGHINOFF
**Prénom :** Lily
**Campus :** ESGI Aix-en-Pce – ESGI 3

## Partie A

| Règle | Description du défaut | Test qui le prouve |
|-------|-----------------------|--------------------|
| C3 | `crediter` acceptait un montant de 0 (contrôle `< 0` au lieu de `<= 0`) | `C3_crediter_montant_non_positif_refuse` |
| C5 | Le débit jusqu'à la limite exacte du découvert était refusé (ex. solde 100 €, découvert 200 €, débit de 300 € refusé) | `C5_debit_jusqu_a_la_limite_accepte` |
| C6 | `estVide` utilisait `equals`, donc un solde de `0.00` n'était pas considéré comme vide | `C6_estVide_vrai_quelle_que_soit_l_ecriture_de_zero` |
| F3 | Un titulaire de 26 ans était exonéré de frais de tenue (`<=` au lieu de `<`) | `F3_tenue_offerte_avant_26_ans` |
| F4 | Un solde moyen de 1 000 € pile n'était pas exonéré (`> 0` au lieu de `>= 0`) | `F4_tenue_offerte_si_solde_moyen_au_moins_1000` |
| F5 | Le supplément premium de 5 € n'était pas ajouté quand la tenue était offerte (`return` anticipé) | `F5_carte_premium_toujours_due` |

**Remarques sur l'historique :**
- Pour `Compte`, les trois corrections (C3, C5, C6) ont été regroupées dans un seul commit `fix:`, juste après le commit `test:` qui contient les tests.
- Les tests de `CalculFrais` sont dans le commit `0b2d2b8`. Le commit suivant, `a8e0c19`, contient la correction de `CalculFrais` (F3, F4, F5) : il aurait dû être préfixé `fix:` et non `test :` (erreur de message de commit).

## Couverture de code

(capture à ajouter à la fin du projet)

## Règles non terminées (partie B)

(à csompléter à la fin du projet)