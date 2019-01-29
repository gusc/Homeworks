`timescale 1ns / 1ps

module LED_Switch(
	output led,
	input sw
	);

	assign led = sw;

endmodule

module LED_Xor(
	output led,
	input sw1, sw2
	);

	// led = sw1 XOR sw2
	assign led = (sw1 ^ sw2);
	 
endmodule

module LED_Timer(
	output led,
	input sw, c
	);
	
	reg [25:0] counter;
	reg [0:0] on = 0;
	
	always @(posedge c)
	begin
		if (counter < 25000000)
			counter <= counter + 1;
		else
		begin
			counter <= 0;
			on = ~on;
		end
	end

	// led = on AND sw
	assign led = (on & sw);
	 
endmodule

module LEDs(
	output led_0, led_1, led_2,
	input sw_0, sw_1, sw_2, sw_3,
	input clk
	);
	
	LED_Switch onOff(.led(led_0), .sw(sw_0));
	LED_Xor xorLogic(.led(led_1), .sw1(sw_1), .sw2(sw_2));
	LED_Timer blink(.led(led_2), .sw(sw_3), .c(clk));
	
endmodule
