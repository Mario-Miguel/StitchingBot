//Medidas
radio_varilla = 4;
largo_varilla = 300;

module varillaRoscada(screw_center, screw_translation, screw_height){
    //Bottom screws
    translate([screw_center,screw_translation,screw_height]){
        rotate([90, 0, 0]){
            cylinder(h=largo_varilla, r=radio_varilla, center=true, $fn=360);
        }
    }
}

//varillaRoscada(0, 0, 0);