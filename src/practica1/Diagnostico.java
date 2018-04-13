package practica1;

import java.io.InputStreamReader;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.mysql.jdbc.Statement;

public class Diagnostico {

	String drv = "com.mysql.jdbc.Driver";
	String serverAddress = "localhost:3306";
	String db = "diagnostico";
	String user = "bddx";
	String pass = "bddx_pwd";
	String url = "jdbc:mysql://" + serverAddress + "/";// 

	private final String DATAFILE = "data/disease_data.data";

	private void showMenu() {

		int option = -1;
		do {
			System.out.println("Bienvenido a sistema de diagnóstico\n");
			System.out.println("Selecciona una opción:\n");
			System.out.println("\t1. Creación de base de datos y carga de datos.");
			System.out.println("\t2. Realizar diagnóstico.");
			System.out.println("\t3. Listar síntomas de una enfermedad.");
			System.out.println("\t4. Listar enfermedades y sus códigos asociados.");
			System.out.println("\t5. Listar síntomas existentes en la BD y su tipo semántico.");
			System.out.println("\t6. Mostrar estadísticas de la base de datos.");
			System.out.println("\t7. Salir.");
			try {
				option = readInt();
				switch (option) {
				case 1:
					crearBD();
					break;
				case 2:
					realizarDiagnostico();
					break;
				case 3:
					listarSintomasEnfermedad();
					break;
				case 4:
					listarEnfermedadesYCodigosAsociados();
					break;
				case 5:
					listarSintomasYTiposSemanticos();
					break;
				case 6:
					mostrarEstadisticasBD();
					break;
				case 7:
					exit();
					break;
				}
			} catch (Exception e) {
				System.err.println("Opción introducida no válida!");
			}
		} while (option != 7);
		exit();
	}

	private void exit() {
		System.out.println("Saliendo.. ¡hasta otra!");
		System.exit(0);
	}

	private Connection conectar() {
		
		Connection conn = null;
		
		try {
			Class.forName(drv);
			conn = DriverManager.getConnection(url, user, pass);//tres parametros: URL siempre igual; el user, para la practica usar uno diferente; la BD, para la practica quitar la BD por que no existe
			System.out.println("Conectado a la base de datos!");

		} catch (Exception e) {
			System.err.println("Error al conectar a la BD: " + e.getMessage());
		}//antes de ejecutar las opciones del menú en la práctica, no me conecto!!
	
		 return conn;
	}
	
	private void crearBD() {
				
		try {
			Class.forName(drv);
			Connection conn = DriverManager.getConnection(url, user, pass);//tres parametros: URL siempre igual; el user, para la practica usar uno diferente; la BD, para la practica quitar la BD por que no existe
			System.out.println("Conectado a la base de datos!");
			
			conn.setAutoCommit(false);//se deshabilita el modo de confirmación automática para iniciar transacción
						//----creo las tablas:
			Statement st = (Statement) conn.createStatement();//crea un objeto para mandar a la base de datos una consulta
			//el statement es mejor crearlo y cerrarlo para cada método

			String creadiagnostico= "create schema diagnostico;";
			String creadisease= "CREATE TABLE `diagnostico`.`disease` (  `disease_id` VARCHAR(45) NOT NULL,  `name` VARCHAR(255) NULL,  PRIMARY KEY (`disease_id`))  ENGINE = InnoDB;";
			String creasymptom= "CREATE TABLE `diagnostico`.`symptom` (  `cui` VARCHAR(25) NOT NULL,  `name` VARCHAR(255) NULL,  PRIMARY KEY (`cui`))  ENGINE = InnoDB;";
			String creadisease_symptom= "CREATE TABLE `diagnostico`.`disease_symptom` (  `disease_id` VARCHAR(45) NOT NULL,  `symptom_id` VARCHAR(25) NOT NULL,  PRIMARY KEY (`disease_id`, `symptom_id`), INDEX `fk2_idx` (`symptom_id` ASC),  CONSTRAINT `fk1` FOREIGN KEY (`disease_id`) REFERENCES `diagnostico`.`disease` (`disease_id`)    ON DELETE NO ACTION    ON UPDATE NO ACTION, CONSTRAINT `fk2` FOREIGN KEY (`symptom_id`) REFERENCES `diagnostico`.`symptom` (`cui`) ON DELETE NO ACTION ON UPDATE NO ACTION) ENGINE = InnoDB;";
			String creasemantic_type= "CREATE TABLE `diagnostico`.`semantic_type` (`semantic_type_id` INT NOT NULL, `tui` VARCHAR(45) NULL, PRIMARY KEY (`semantic_type_id`))  ENGINE = InnoDB;";
			String creasymptom_semantic_type= "CREATE TABLE `diagnostico`.`symptom_semantic_type` ( `cui` VARCHAR(25) NOT NULL, `semantic_type_id` INT NOT NULL, PRIMARY KEY (`cui`, `semantic_type_id`), INDEX `fk2_idx` (`semantic_type_id` ASC), CONSTRAINT `fk1` FOREIGN KEY (`cui`) REFERENCES `diagnostico`.`symptom` (`cui`) ON DELETE NO ACTION ON UPDATE NO ACTION, CONSTRAINT `fk2` FOREIGN KEY (`semantic_type_id`) REFERENCES `diagnostico`.`semantic_type` (`semantic_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION) ENGINE = InnoDB;";
			String creacode= "CREATE TABLE `diagnostico`.`code` (`code` VARCHAR(255) NOT NULL, `source_id` VARCHAR(25) NOT NULL,  PRIMARY KEY (`code`, `source_id`))  ENGINE = InnoDB;";
			String creasource= "CREATE TABLE `diagnostico`.`source` ( `source_id` VARCHAR(25) NOT NULL,  `name` VARCHAR(255) NULL,  PRIMARY KEY (`source_id`))  ENGINE = InnoDB;";
			String fkcode= "ALTER TABLE `diagnostico`.`code` ADD INDEX `code_fk1_idx` (`source_id` ASC);ALTER TABLE `diagnostico`.`code` ADD CONSTRAINT `code_fk1`  FOREIGN KEY (`source_id`)  REFERENCES `diagnostico`.`source` (`source_id`)  ON DELETE NO ACTION   ON UPDATE NO ACTION;";
			String creadisease_code= "CREATE TABLE `diagnostico`.`disease_code` (  `disease_id` VARCHAR(45) NOT NULL,  `code` VARCHAR(255) NOT NULL,  PRIMARY KEY (`disease_id`, `code`),  INDEX `code_fk4_idx` (`code` ASC),  CONSTRAINT `code_fk3`    FOREIGN KEY (`disease_id`)    REFERENCES `diagnostico`.`disease` (`disease_id`)    ON DELETE NO ACTION    ON UPDATE NO ACTION,  CONSTRAINT `code_fk4`    FOREIGN KEY (`code`)    REFERENCES `diagnostico`.`code` (`code`) ON DELETE NO ACTION ON UPDATE NO ACTION) ENGINE = InnoDB;";
			
			int result = st.executeUpdate(creadiagnostico);//aqui va la sentencia SQL
			int result2 = st.executeUpdate(creadisease);
			int result3 = st.executeUpdate(creasymptom);
			int result4 = st.executeUpdate(creadisease_symptom);
			int result5 = st.executeUpdate(creasemantic_type);
			int result6 = st.executeUpdate(creasymptom_semantic_type);
			int result7 = st.executeUpdate(creacode);
			int result8 = st.executeUpdate(creasource);
			int result9 = st.executeUpdate(fkcode);
			int result10 = st.executeUpdate(creadisease_code);

			System.out.println("Tablas creadas!");
			
			//---inserto los datos:
			
			LinkedList<String> data= readData();
			System.out.println("Mi archivo es: " + data);
			String[] parts= data.split(",");

			
			System.out.println("Número de filas afectadas: " + result2);//devuelve el numero de tuplas (filas) actualizadas, no el numero de columnas,
			System.out.println("Query ejecutada!");
			//----- Finaliza la creación de tablas e inserción de los datos
			
			conn.commit();//se indica que se deben aplicar los cambios en la base de datos
			st.close();
			conn.setAutoCommit(true);//cambia el AC a uni para finalizar transacción e inciar operaciones individuales

			conn.close();//sólo se cierra la conexiòn cuando se terminan todas las operaciones, se debe mantener activa hasta el final. 
			//se ejecuta en el método exit sólamente 
			
		
		} catch (Exception e) {
			System.err.println("Error al conectar a la BD: " + e.getMessage());
		}//antes de ejecutar las opciones del menú en la práctica, no me conecto!!
	
	}

	private void realizarDiagnostico() {
		// implementar
	}

	private void listarSintomasEnfermedad() {
		// implementar
	}

	private void listarEnfermedadesYCodigosAsociados() {
		// implementar
	}

	private void listarSintomasYTiposSemanticos() {
		// implementar
	}

	private void mostrarEstadisticasBD() {
		// implementar
	}

	/**
	 * Método para leer números enteros de teclado.
	 * 
	 * @return Devuelve el número leído.
	 * @throws Exception
	 *             Puede lanzar excepción.
	 */
	private int readInt() throws Exception {
		try {
			System.out.print("> ");
			return Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		} catch (Exception e) {
			throw new Exception("Not number");
		}
	}

	/**
	 * Método para leer cadenas de teclado.
	 * 
	 * @return Devuelve la cadena leída.
	 * @throws Exception
	 *             Puede lanzar excepción.
	 */
	private String readString() throws Exception {
		try {
			System.out.print("> ");
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (Exception e) {
			throw new Exception("Error reading line");
		}
	}

	/**
	 * Método para leer el fichero que contiene los datos.
	 * 
	 * @return Devuelve una lista de String con el contenido.
	 * @throws Exception
	 *             Puede lanzar excepción.
	 */
	private LinkedList<String> readData() throws Exception {
		LinkedList<String> data = new LinkedList<String>();
		BufferedReader bL = new BufferedReader(new FileReader(DATAFILE));
		while (bL.ready()) {
			data.add(bL.readLine());
		}
		bL.close();
		return data;
	}

	public static void main(String args[]) {
		new Diagnostico().showMenu();
	}
}
