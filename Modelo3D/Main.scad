use<SewingMachine.scad>
use<StitchingBot.scad>

translate([17,0,0])
scale([200,200,200]){
    
    SewingMachine();
}



StitchingBot();