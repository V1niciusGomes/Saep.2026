# Clínica Vet Aurora — Semana 1

Mini sistema desktop (Java + Swing + MySQL / JDBC) do SENAI Blumenau.

## Como rodar

1. **Banco**: no MySQL Workbench, execute `sql/saep_db.sql`.
2. **Conexão**: ajuste `PASS` em `util/ConnectionFactory.java` se a senha do root for diferente.
3. **F5**: abra `app/Main.java` e rode. A tela de login azul abre.
4. **Login**: `admin` / `123456`.

## Ordem de código (1 a 12)
1. sql/saep_db.sql   2. pom.xml   3. util/ConnectionFactory.java
4. util/HashUtil.java   5. model/Usuario.java   6. model/Tutor.java
7. dao/UsuarioDAO.java   8. dao/TutorDAO.java   9. view/LoginView.java
10. view/MenuView.java — AINDA NÃO FEITO
11. view/TutorView.java   12. app/Main.java
