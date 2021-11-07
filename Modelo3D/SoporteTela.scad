module sujeccionTuerca(){
    difference(){
        cube([20,25,25]);
        
        //Hueco varilla
        translate([0,12.5,10])
            rotate([0,90,0])
                cylinder(h=60, r=5.1, center=true, $fn=360);
        
        //Hueco para meter la tuerca
        translate([5, 12.5, 10])
            cube([5.2, 23, 30], center=true);
        
        //Hueco para la extension de la tuerca
        translate([7.5, 7.4, 0])
            cube([20, 10.2, 10]);
        
        //Huecos para los tornillos 
        pos_tornillos_varilla = [[12.5,18], [4.5,10], [21.5,10]];
        for(i=[0:len(pos_tornillos_varilla)-1]){
            huecoTornilloVarilla(
                0, 
                pos_tornillos_varilla[i][0],
                pos_tornillos_varilla[i][1]);
        }
        
//        translate([0,12.5,18])
//            rotate([0,90,0])
//                cylinder(h=60, d=3.6, center=true, $fn=360);
//        translate([0,4.5,10])
//            rotate([0,90,0])
//                cylinder(h=60, d=3.6, center=true, $fn=360);
//        translate([0,21.5,10])
//            rotate([0,90,0])
//                cylinder(h=60, d=3.6, center=true, $fn=360);
    }
    
}

module huecoTornilloVarilla(x, y, z){
    translate([x,y,z])
            rotate([0,90,0])
                cylinder(
                    h=60, 
                    d=3.6, 
                    center=true, 
                    $fn=360
                );
}

module sujeccionTuercaLarga(){
    difference(){
        cube([15,25,65]);
        
        //El de alante
        translate([0,12.5,50])
            rotate([0,90,0])
                cylinder(h=60, r=5.1, center=true, $fn=360);
        
        translate([0,12.5,10])
            rotate([0,90,0])
                cylinder(h=60, d=8.1, center=true, $fn=360);
    }
}

module sujeccionesTuercas(){
    translate([5,0,25])
        rotate([270,0,0]){
            sujeccionTuerca();
            
            translate([90,25,0])
                rotate([0,0,180])
                    sujeccionTuerca();
            translate([37.5,0,-40])
                sujeccionTuercaLarga();
        }
}

module pandereta(){
    

    translate([0,10,0]){
        translate([5,5,0])
            difference(){
                cube([140,90,3]);
                translate([5,5,0])
                roundedCube([130,80,25], 3);
            }
            
        difference(){
            roundedCube([150,100,20], 2);
            translate([5,5,-1])
                roundedCube([140,90,25], 3);
            translate([-1, -1, -3])
                cube([152, 102, 3]);
            translate([-1, -1, 15])
                cube([152, 102, 3]);
        }
    }
    
}

module roundedCube(d,r) {
    minkowski() {
        translate([r,r]) cube([d[0]-2*r, d[1]-2*r, d[2]-2*r]);
        sphere(r, $fn=50);
    }
}

module unionTuercasPandereta(){
    
    hull(){
        translate([5,0,0])
            cube([90, 2, 25]);
        translate([5,8,0])
            cube([90, 5, 12.5]);
    }
}

module sujeccionTela(){
    translate([0,0,0])
        sujeccionesTuercas();
    translate([0,25,0])
        unionTuercasPandereta();
    
//    translate([0,25,0])
//        pandereta();
//    
//    translate([6.5,41.5,10])
//        cierre();
}

module cierre(){
    difference(){
        roundedCube([137,87,17], 2);
        translate([5,5,-1])
            roundedCube([127,77,25], 3);
        translate([-1, -1, -3])
            cube([152, 102, 3]);
        translate([-1, -1, 12])
            cube([152, 102, 3]);
    }
}

sujeccionTela();