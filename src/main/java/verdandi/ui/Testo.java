/*******************************************************************************
 * Copyright 2010 Olaf Sebelin
 * 
 * This file is part of Verdandi.
 * 
 * Verdandi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Verdandi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Verdandi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package verdandi.ui;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;

/**
 *
 * @
 */
public class Testo extends JFrame {
	
	

	private static final long serialVersionUID = 1094745771400277273L;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public Testo() throws HeadlessException {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	
//	private List getExampleWorkDayFromDB() {
//		List  res=new ArrayList();
//		
//		Calendar cal= Calendar.getInstance();
//		cal.set(Calendar.HOUR,8);
//		cal.set(Calendar.MINUTE,0);
//		
//		Random rand = new Random();
//		Map cunitsMap;
//		try {
//			cunitsMap = VerdandiDB.getInstance().getProjects();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return new ArrayList();
//		}
//		StoredProject cunits[] = (StoredProject[]) cunitsMap.values().toArray(new StoredProject[0]);
//		
//		
//	
//		return res;
//	}

	
	
	
	
	
	
	public static void main(String[] args) {
		BasicConfigurator.configure();

    Font fnt[]= GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    for (int i = 0; i < fnt.length; i++) {
      System.out.println(fnt[i].getName()+": "+fnt[i]);
    }
    //Font f= Font.getFont("Utopia");
		
	}
}
