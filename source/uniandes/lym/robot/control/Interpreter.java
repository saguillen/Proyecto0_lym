package uniandes.lym.robot.control;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import uniandes.lym.robot.kernel.*;



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

	private void metodoCmds(String[] cmds) {

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



			}else if (comando.startsWith("move"))
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
			//			else if( comando.startsWith(turn))
//			Ejemplo

//ROBOT_R
//VARS a, b
//BEGIN
//assign(a ,3);
//assign(b ,1);
//turn(right);
//move(b);
//END
			//System.out.println(comando);



		}

	}

}
