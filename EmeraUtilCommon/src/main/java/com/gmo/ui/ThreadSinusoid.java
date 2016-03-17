//*******************************************************************
//
//      Oce Engineering Creteil: PRODUCT DOCUMENT        CONFIDENTIAL
//
//       R&D-filename    :   %name%
//
//       Title           :
//       Abstract        :
//
//
//
//
//       Keywords        :
//
//       %version:       %
//       %created_by:    %
//       %date_created:  %
//       %full_filespec: %
//
//   Copyright 2004 Oce-Technologies B.V., Venlo, The Netherlands
//
//*******************************************************************
package com.gmo.ui;



public class ThreadSinusoid extends Thread {

	private ISinusoidEffect associated_component;
	private int vitesse;
	private int maxLightening;
	/**
	 * 
	 * @param associated_component composant associe a l'animation
	 * @param vitesse valeur comprise entre 0 et 360. represente la variation de luminosite entre chaque changement d'image en degres.
	 */
	public ThreadSinusoid(ISinusoidEffect associated_component, int vitesse, int maxLightening){
		this.associated_component = associated_component;
		this.setName("ThreadSinusoid for visual effects applied to component " + associated_component.getComponentName());
		setPriority(MIN_PRIORITY);
		this.vitesse = vitesse;
		this.maxLightening = maxLightening;
		if(this.maxLightening > 255){
			this.maxLightening = 255;
		}else if(this.maxLightening < 50){
			this.maxLightening = 50;
		}
	}

	public void run() {
		
		int aValue = 0;
		
		while (true) {
			try {
				
				Thread.sleep(20);
				aValue += vitesse;
				if(aValue > 360 ) aValue = 0;
					
				associated_component.setEllapsedActionTimeValue((int) ((1-(Math.sin(aValue*Math.PI/180) * 0.5 + 0.5)) * maxLightening));
			} catch (InterruptedException e) {

			}
		}
	}
}
