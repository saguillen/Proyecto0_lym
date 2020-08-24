package uniandes.lym.robot.control;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import uniandes.lym.robot.kernel.*;

//Integrantes: - Santiago Mora Felix_seccion_1

//				- Sergio Andres Guillen Fonseca_seccion_2

//IMPORTANTE: leer Readme.txt en la carpeta lib.

/**
 * Receives commands and relays them to the Robot. 
 */

public class Interpreter   {

	/**
	 * Robot's world
	 */
	private RobotWorldDec world;   

	private class Tupla {

		String nombre;
		int cant;

		public Tupla(String nombre){
			this.nombre = nombre;
		}

		void sumar(int cantidad) {
			cant += cantidad;
		}
	}

	private ArrayList<Tupla> tuplas;

	public Interpreter()
	{
		tuplas = new ArrayList<Tupla>();
	}


	/**
	 * Creates a new interpreter for a given world
	 * @param world 
	 */


	public Interpreter(RobotWorld mundo)
	{
		this.world =  (RobotWorldDec) mundo;

	}


	/**
	 * sets a the world
	 * @param world 
	 */

	public void setWorld(RobotWorld m) 
	{
		world = (RobotWorldDec) m;

	}



	/**
	 *  Processes a sequence of commands. A command is a letter  followed by a ";"
	 *  The command can be:
	 *  M:  moves forward
	 *  R:  turns right
	 *  
	 * @param input Contiene una cadena de texto enviada para ser interpretada
	 */

	public String process(String input) throws Error
	{   


		StringBuffer output=new StringBuffer("SYSTEM RESPONSE: -->\n");	
		/**
		int i;
		int n;
		boolean ok = true;
		n= input.length();
		i  = 0;
		 **/
		try	    {
			if(input.startsWith("ROBOT_R")&&input.contains("BEGIN")&&input.endsWith("END")){
				String dospartes = input.substring(7);
				String[] loquehacer = dospartes.split("BEGIN");
				String[] cmds;
				if(dospartes.contains("VARS")) {
					String lineaconvars = loquehacer[0].substring(5);
					String[] nombresVars = lineaconvars.split(",");
					for(String nombre: nombresVars) {
						String limp = nombre.replace(" ", "");
						limp=limp.replaceAll("\n", "");
						nombre = limp;
						System.out.println(nombre);
						Tupla var = new Tupla(nombre);
						tuplas.add(var);
					}
				}
				cmds = loquehacer[1].split(";");
				//				System.out.println(cmds[0]);
				metodoCmds(cmds);
			}
			else throw new Exception("Unrecognized command");
			output.append("Instructions executed.");
		}
		catch (Exception e ){
			output.append("Error!!!  "+e.getMessage());
		}
		return output.toString();
		/**
			while (i < n &&  ok) {
				switch (input.charAt(i)) {
				case 'M': world.moveForward(1); output.append("move \n");break;
				case 'R': world.turnRight(); output.append("turnRignt \n");break;
				case 'C': world.putChips(1); output.append("putChip \n");break;
				case 'B': world.putBalloons(1); output.append("putBalloon \n");break;
				case  'c': world.pickChips(1); output.append("getChip \n");break;
				case  'b': world.grabBalloons(1); output.append("getBalloon \n");break;
				default: output.append(" Unrecognized command:  "+ input.charAt(i)); ok=false;
				}

				if (ok) {
					if  (i+1 == n)  { output.append("expected ';' ; found end of input; ");  ok = false ;}
					else if (input.charAt(i+1) == ';') 
					{
						i= i+2;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							System.err.format("IOException: %s%n", e);
						}

					}
					else {output.append(" Expecting ;  found: "+ input.charAt(i+1)); ok=false;
					}
				}


			}

		}
		 **/

	}

	private void metodoCmds(String[] cmds) throws Error, Exception {

		Tupla t = null;

		for(String comando	: cmds)
		{
			String limp = comando.replace("\n", ""); //limpia saltos de linea y espacios
			limp = limp.replace(" ", "");
			comando = limp;


			if (comando.endsWith("END"));
			{
				String quitar = comando.replace("END", "");
				comando = quitar;

			}
			if (comando.contains("assign"))
			{
				comando = comando.substring(comando.indexOf("(")+1,comando.indexOf(")"));
				String[] asignar = comando.split(",");
				//				System.out.println(comando);
				//				System.out.println("si entra");

				int numero = Integer.parseInt(asignar[1]);

				if(numero!=0)
				{
					boolean esVariable = false;

					for(int i = 0; i<tuplas.size() && esVariable==false; i++)
					{
						if(tuplas.get(i).nombre.equals(asignar[0]))
						{
							tuplas.get(i).sumar(numero);
							esVariable=true;
							t = tuplas.get(i);
						}
					}
					if(esVariable==true)
					{
						System.out.println(t.cant+"");
					}else{
						System.out.println("No es variable declarada");
					}
				}



			}else if (comando.startsWith("move")&&!comando.contains("Dir"))
			{
				int numero = 0;
				comando= comando.substring(comando.indexOf("(")+1,comando.indexOf(")"));

				boolean esVariable = false;

				for(int i = 0; i< tuplas.size()&&esVariable==false; i++)
				{
					if(tuplas.get(i).nombre.equals(comando))
					{
						numero=tuplas.get(i).cant;
						esVariable = true;
					}
				}
				if(esVariable==false)
				{
					numero = Integer.parseInt(comando);
				}
				world.moveForward(numero);

			}else if (comando.startsWith("turn"))
			{
				comando= comando.substring(comando.indexOf("(")+1,comando.indexOf(")"));
				if(comando.equals("right"))
				{
					world.turnRight();
				}
				else if(comando.equals("left"))
				{
					world.turnRight();
					world.turnRight();
					world.turnRight();
				}
				else if(comando.equals("around"))
				{
					world.turnRight();
					world.turnRight();
				}
				else{
					System.out.println("No hay direccion");
				}
			}
			else if(comando.startsWith("face")) {
				comando = comando.substring(comando.indexOf('(')+1,comando.indexOf(')'));
				int orientacion = world.getFacing();
				if(comando.equals("north")) {
					if(orientacion == 1){
						world.turnRight();
						world.turnRight();
					}
					else if(orientacion == 2){
						world.turnRight();
						world.turnRight();
						world.turnRight();
					}
					else if(orientacion == 3){
						world.turnRight();
					} //Salu2.
				}
				else if(comando.equals("south")) {
					if(orientacion == 0){
						world.turnRight();
						world.turnRight();
					}
					else if(orientacion == 2){
						world.turnRight();
					}
					else if(orientacion == 3){
						world.turnRight();
						world.turnRight();
						world.turnRight();
					}
				}
				else if(comando.equals("east")) {
					if(orientacion == 0){ 
						world.turnRight();
					}
					else if(orientacion == 3){ 
						world.turnRight();
						world.turnRight();
					}
					else if(orientacion == 1){ 
						world.turnRight();
						world.turnRight();
						world.turnRight();
					}
				}
				else if(comando.equals("west")) {
					if(orientacion == 0){
						world.turnRight();
						world.turnRight();
						world.turnRight();
					}
					else if(orientacion == 1){
						world.turnRight();

					}
					else if(orientacion == 2){
						world.turnRight();
						world.turnRight();
					}
				}
				else System.out.println("Expected \"north\", \"south\", \"east\" or \"west\", got " + comando);
			}
			else if(comando.startsWith("put")) {
				comando = comando.substring(comando.indexOf('(') + 1, comando.indexOf(')'));
				String[] partes = comando.split(",");
				partes[1]= partes[1].trim();
				boolean esVariable = false;
				int cant = 0;
				Iterator<Tupla> iter = tuplas.iterator();
				Tupla actual;
				while(iter.hasNext() && !esVariable) {
					actual = iter.next();
					if(actual.nombre.equals(partes[0])){
						cant = actual.cant;
						esVariable = true;
					}
				}
				if(!esVariable) {
					try {
						cant = Integer.parseInt(partes[0]);
					}
					catch(Exception e) {
						System.out.println("Number or variable name expected.");
					}
				}
				if(cant>0) {
					if(partes[1].equals("Balloons")) {
						if(world.getMyBalloons()<cant) {
							System.out.println("Not enough balloons.");
						}
						else {
							try {
								world.putBalloons(cant);
							}
							catch(Exception e) {
								System.out.println("Jsjsjsj hay un error. F.");
							}
						}
					}
					else if(partes[1].equals("Chips")){
						if(world.getMyChips()<cant) {
							System.out.println("Not enough chips.");
						}
						else {
							try {
								world.putChips(cant);
							}
							catch (Exception e) {
								System.out.println("Jsjsjs tienes un error k gei.");
							}
						}
					}
					else {
						System.out.println("\"Chips\" or \"Balloons\" expected. Got " + partes[1]);
					}
				}
			}
			else if(comando.startsWith("pick")) {
				comando = comando.substring(comando.indexOf('(') + 1, comando.indexOf(')'));
				String[] partes = comando.split(",");
				partes[1]= partes[1].trim();
				boolean esVariable = false;
				int cant = 0;
				Iterator<Tupla> iter = tuplas.iterator();
				Tupla actual;
				while(iter.hasNext() && !esVariable) {
					actual = iter.next();
					if(actual.nombre.equals(partes[0])){
						cant = actual.cant;
						esVariable = true;
					}
				}
				if(!esVariable) {
					try {
						cant = Integer.parseInt(partes[0]);
					}
					catch(Exception e) {
						System.out.println("Number or variable name expected.");
					}
				}
				if(cant>0) {
					if(partes[1].equals("Balloons")) {
						try {
							world.grabBalloons(cant);
						}
						catch(Exception e) {
							System.out.println("Jsjsjsj hay un error. F.");
						}

					}
					else if(partes[1].equals("Chips")){
						try {
							world.pickChips(cant);
						}
						catch (Exception e) {
							System.out.println("Jsjsjs tienes un error k gei.");
						}

					}
					else {
						System.out.println("\"Chips\" or \"Balloons\" expected. Got " + partes[1]);
					}
				}
			}
			else if(comando.startsWith("skip")) continue;

			else if(comando.startsWith("moveDir"))
			{
				comando = comando.substring(comando.indexOf('(') + 1, comando.indexOf(')'));

				String[] partes = comando.split(",");

				boolean esVariable = false;

				int cant = 0;
				Iterator<Tupla> iter = tuplas.iterator();
				Tupla actual;
				while(iter.hasNext() && !esVariable) {
					actual = iter.next();
					if(actual.nombre.equals(partes[0])){
						cant = actual.cant;
						esVariable = true;
					}
				}
				if(!esVariable) {
					try {
						cant = Integer.parseInt(partes[0]);
					}
					catch(Exception e) {
						System.out.println("Number or variable name expected.");
					}
				}
				if(cant>0) {
					if(partes[1].equals("front")) {

						world.moveForward(cant);


					}
					else if(partes[1].equals("right"))
					{
						world.turnRight();
						world.moveForward(cant);
						world.turnRight();
						world.turnRight();
						world.turnRight();
					}
					else if(partes[1].equals("left"))
					{
						world.turnRight();
						world.turnRight();
						world.turnRight();
						world.moveForward(cant);
						world.turnRight();
					}
					else if (partes[1].equals("back"))
					{
						world.turnRight();
						world.turnRight();
						world.moveForward(cant);
						world.turnRight();
						world.turnRight();

					}else System.out.println("Impossible to move n Steps Dir");

				}


			}else if(comando.contains("InDir"))
			{
				comando = comando.substring(comando.indexOf('(') + 1, comando.indexOf(')'));

				String[] partes = comando.split(",");

				boolean esVariable = false;

				int cant = 0;
				Iterator<Tupla> iter = tuplas.iterator();
				Tupla actual;
				while(iter.hasNext() && !esVariable) {
					actual = iter.next();
					if(actual.nombre.equals(partes[0])){
						cant = actual.cant;
						esVariable = true;
					}
				}
				if(!esVariable) {
					try {
						cant = Integer.parseInt(partes[0]);
					}
					catch(Exception e) {
						System.out.println("Number or variable name expected.");
					}
				}
				System.out.println("Si es numero");
				if(cant>0)
				{
					if(partes[1].equals("north"))
					{
						if(world.facingEast())
						{
							world.turnRight();
							world.turnRight();
							world.turnRight();
							world.moveForward(cant);
						}

						else if(world.facingSouth())
						{
							world.turnRight();
							world.turnRight();
							world.moveForward(cant);
						}
						else if(world.facingWest())
						{
							world.turnRight();
							world.moveForward(cant);
						}
						else
						{
							world.moveForward(cant);
						}
					}else if(partes[1].equals("south"))
					{
						if(world.facingEast())
						{
							world.turnRight();
							world.moveForward(cant);
						}else if(world.facingNorth())
						{
							world.turnRight();
							world.turnRight();
							world.moveForward(cant);
						}else if(world.facingWest())
						{
							world.turnRight();
							world.turnRight();
							world.turnRight();
							world.moveForward(cant);
						}
						else{world.moveForward(cant);}

					}else if(partes[1].equals("east"))
					{
						if(world.facingSouth())
						{
							world.turnRight();
							world.turnRight();
							world.turnRight();
							world.moveForward(cant);
						}else if(world.facingWest())
						{
							world.turnRight();
							world.turnRight();
							world.moveForward(cant);
						}else if(world.facingNorth())
						{
							world.turnRight();
							world.moveForward(cant);
						}else{
							world.moveForward(cant);
						}
					}else if(partes[1].equals("west"))
					{
						if(world.facingNorth())
						{
							world.turnRight();
							world.turnRight();
							world.turnRight();
							world.moveForward(cant);
						}else if(world.facingEast())
						{
							world.turnRight();
							world.turnRight();
							world.moveForward(cant);
						}else if(world.facingSouth())
						{
							world.turnRight();
							world.moveForward(cant);
						}else{
							world.moveForward(cant);}
					}
					else{System.out.println("Not a direction");};


				}
				//			else if( comando.startsWith(turn))
				//			Ejemplo

				//				ROBOT_R
				//				VARS a, b
				//				BEGIN
				//				assign(a ,3);
				//				assign(b ,1);
				//				turn(right);
				//				move(b);
				//				moveInDir(b,south);
				//				END
				//System.out.println(comando);
			}else if(comando.startsWith("facing"))
			{
				comando = comando.substring(comando.indexOf('(') + 1, comando.indexOf(')'));
				boolean apunta = facing(comando);
				//				comando = apunta;
			}
			else if(comando.startsWith("{")&&comando.startsWith("}"))
			{
				try{
					comando = comando.substring(comando.indexOf('{') + 1, comando.indexOf('}'));
					String[] partes = comando.split(";");
					while(checkCond(partes[1],partes))
					{
						metodoCmds(partes);
					}
				}
				catch (Exception e)
				{
					e.getMessage();
				}

			}
		}
	}

	public boolean checkCond(String cond, String[] comandos)
	{
		boolean condicion= false;

		if(cond.startsWith("facing"))
		{
			cond = cond.substring(cond.indexOf('(') + 1, cond.indexOf(')'));
			condicion = facing(cond);
		}
		if(cond.startsWith("move")&&!cond.contains("Dir")){
			cond = cond.substring(cond.indexOf('(') + 1, cond.indexOf(')'));
			condicion = checkMove(cond);
		}
		if(cond.contains("InDir"))
		{
			cond = cond.substring(cond.indexOf('(') + 1, cond.indexOf(')'));

			String[] partes = cond.split(",");

			boolean esVariable = false;

			int cant = 0;
			Iterator<Tupla> iter = tuplas.iterator();
			Tupla actual;
			while(iter.hasNext() && !esVariable) {
				actual = iter.next();
				if(actual.nombre.equals(partes[0])){
					cant = actual.cant;
					esVariable = true;
				}
			}
			if(!esVariable) {
				try {
					cant = Integer.parseInt(partes[0]);
				}
				catch(Exception e) {
					System.out.println("Number or variable name expected.");
				}
			}

			condicion = checkMoveInDir(cant, partes[1]);
		}
		if(cond.startsWith("moveDir"))
		{
			cond = cond.substring(cond.indexOf('(') + 1, cond.indexOf(')'));

			String[] partes = cond.split(",");

			boolean esVariable = false;

			int cant = 0;
			Iterator<Tupla> iter = tuplas.iterator();
			Tupla actual;
			while(iter.hasNext() && !esVariable) {
				actual = iter.next();
				if(actual.nombre.equals(partes[0])){
					cant = actual.cant;
					esVariable = true;
				}
			}
			if(!esVariable) {
				try {
					cant = Integer.parseInt(partes[0]);
				}
				catch(Exception e) {
					System.out.println("Number or variable name expected.");
				}
			}

			condicion = checkMoveInDir(cant, partes[1]);
		}
		if(cond.startsWith("put"))
		{
			cond = cond.substring(cond.indexOf('(') + 1, cond.indexOf(')'));
			String[] partes = cond.split(",");
			partes[1]= partes[1].trim();
			boolean esVariable = false;
			int cant = 0;
			Iterator<Tupla> iter = tuplas.iterator();
			Tupla actual;
			while(iter.hasNext() && !esVariable) {
				actual = iter.next();
				if(actual.nombre.equals(partes[0])){
					cant = actual.cant;
					esVariable = true;
				}
			}
			if(!esVariable) {
				try {
					cant = Integer.parseInt(partes[0]);
				}
				catch(Exception e) {
					System.out.println("Number or variable name expected.");
				}
			}
			condicion = checkPut(cant, partes[1]);
		}
		if(cond.startsWith("pick"))
		{
			cond = cond.substring(cond.indexOf('(') + 1, cond.indexOf(')'));
			String[] partes = cond.split(",");
			partes[1]= partes[1].trim();
			boolean esVariable = false;
			int cant = 0;
			Iterator<Tupla> iter = tuplas.iterator();
			Tupla actual;
			while(iter.hasNext() && !esVariable) {
				actual = iter.next();
				if(actual.nombre.equals(partes[0])){
					cant = actual.cant;
					esVariable = true;
				}
			}
			if(!esVariable) {
				try {
					cant = Integer.parseInt(partes[0]);
				}
				catch(Exception e) {
					System.out.println("Number or variable name expected.");
				}
			}
			condicion = checkPick(cant, partes[1]);
		}


		return condicion;

	}

	public boolean checkMove(String num)
	{
		boolean condicion= false;

		condicion =! world.blockedInRange(world.getPosition().x, world.getPosition().y, Integer.parseInt(num), world.getFacing());
		return condicion;

	}

	public boolean checkMoveInDir(int cant, String direccion)
	{
		boolean condicion= false;
		int dir=0;
		if(direccion =="north")
		{
			dir = 0;
		}else if(direccion == "south")
		{
			dir = 1;
		}else if(direccion == "east")
		{
			dir = 2;
		}else if(direccion == "west")
		{
			dir = 3;
		}

		if(direccion=="front"||direccion=="left"||direccion=="right"||direccion=="back")
		{
			dir = world.getFacing();
		}


		condicion= !world.blockedInRange(world.getPosition().x, world.getPosition().y, cant, dir);
		return condicion;
	}
	public boolean checkPut(int cant, String partes)
	{
		boolean cond = false;
		if(cant>0) {
			if(partes.equals("Balloons")) {
				if(world.getMyBalloons()<cant) {
					System.out.println("Not enough balloons.");
				}
				else {
					try {
						world.putBalloons(cant);
						cond = true;
					}
					catch(Exception e) {
						System.out.println("Jsjsjsj hay un error. F.");
						cond =false;
					}
				}
			}
			else if(partes.equals("Chips")){
				if(world.getMyChips()<cant) {
					System.out.println("Not enough chips.");
				}
				else {
					try {
						world.putChips(cant);
						cond = true;
					}
					catch (Exception e) {
						System.out.println("Jsjsjs tienes un error k gei.");
						cond = false;
					}
				}
			}
			else {
				System.out.println("\"Chips\" or \"Balloons\" expected. Got " + partes);
			}
		}
		return cond;
	}
	public boolean checkPick(int cant, String partes)
	{
		boolean cond = false;

		if(cant>0) {
			if(partes.equals("Balloons")) {
				try {
					world.grabBalloons(cant);
					cond = true;
				}
				catch(Exception e) {
					System.out.println("Jsjsjsj hay un error. F.");
					cond = false;
				}

			}
			else if(partes.equals("Chips")){
				try {
					world.pickChips(cant);
					cond = true;
				}
				catch (Exception e) {
					System.out.println("Jsjsjs tienes un error k gei.");
					cond = false;
				}

			}
			else {
				System.out.println("\"Chips\" or \"Balloons\" expected. Got " + partes);
			}
		}
		return cond;

	}


	public boolean facing(String dir)
	{
		if(dir.equals("north"))
		{
			return world.facingNorth();
		}else if(dir.equals("south"))
		{
			return world.facingSouth();
		}else if(dir.equals("east"))
		{
			return world.facingEast();
		}else if(dir.equals("west"))
		{
			return world.facingWest();
		}
		return false;

	}
}
