`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Engineer: Gusts Kaksis (gk17025)
// 
// Create Date:    13:12:59 05/02/2018 
// Design Name: 
// Module Name:    VGA 
// Project Name: 
// Target Devices: 
// Tool versions: 
// Description: 
//
// Dependencies: 
//
// Revision: 
// Revision 0.01 - File Created
// Additional Comments: 
//
//////////////////////////////////////////////////////////////////////////////////
module VGA(
	input CLK_50MHZ,
	output VGA_RED,
	output VGA_GREEN,
	output VGA_BLUE,
	output VGA_HSYNC,
	output VGA_VSYNC
    );

	// VGA timing constants
	// URL: 
	parameter HBPORCH = 96;
	parameter HDISPLAY = 144;
	parameter HFPORCH = 784;
	parameter HEND = 800;
	parameter VBPORCH = 2;
	parameter VDISPLAY = 12;
	parameter VFPORCH = 492;
	parameter VEND = 521;

	// Sync state
	reg[0:0] hSync = 0;
	reg[0:0] vSync = 0;
	// Signal location (x:y)
	reg[9:0] hClock = 0;
	reg[9:0] vClock = 0;
	reg[1:0] inScreen = 0;
	integer xScreen = 0;
	integer yScreen = 0;
	integer xBoard = 0;
	integer yBoard = 0;
	reg[0:0] color = 0;
	
	// Pixel clock @25MHz
	reg[0:0] pxClock = 0;
	always @(posedge CLK_50MHZ) begin
		pxClock <= ~pxClock;
	end
	
	// Work with pixel clock
	always @(posedge pxClock) begin
		hClock = hClock + 1;
		if (hClock >= HEND) begin
			// New line
			hClock = 0;
			hSync = 0;
			vClock = vClock + 1;
		end
		else if (hClock >= HFPORCH) begin
			// Exit horizontal display phase
			inScreen = (inScreen & ~1);
		end
		else if (hClock >= HDISPLAY) begin
			// Entering horizontal display phase
			inScreen = (inScreen | 1);
		end
		else if (hClock >= HBPORCH) begin
			// End of h-sync pulse
			hSync = 1;
		end
		
		if (vClock >= VEND) begin
			// New frame
			vClock = 0;
			vSync = 0;
		end
		else if (vClock >= VFPORCH) begin
			// Exit vertical display phase
			inScreen = (inScreen & ~2);
		end
		else if (vClock >= VDISPLAY) begin
			// Entering vertical display phase
			inScreen = (inScreen | 2);
		end
		else if (vClock >= VBPORCH) begin
			// End of h-sync pulse
			vSync = 1;
		end
		
		color = 0;
		if (inScreen == 3) begin
			// Draw something
			xScreen = hClock - HDISPLAY;
			yScreen = vClock - VDISPLAY;

			if (xScreen > 80 && xScreen < 560) begin
				// Board needs to be off-set by 80px horizontally to be centered in screen
				xScreen = xScreen - 80;
				// Single cell is 480/8=60px, too bad we can't use division or while loops here :(
				xBoard = 0;
				if (xScreen > 420) xBoard = 7;
				else if (xScreen > 360) xBoard = 6;
				else if (xScreen > 300) xBoard = 5;
				else if (xScreen > 240) xBoard = 4;
				else if (xScreen > 180) xBoard = 3;
				else if (xScreen > 120) xBoard = 2;
				else if (xScreen > 60) xBoard = 1;
				yBoard = 0;
				if (yScreen > 420) yBoard = 7;
				else if (yScreen > 360) yBoard = 6;
				else if (yScreen > 300) yBoard = 5;
				else if (yScreen > 240) yBoard = 4;
				else if (yScreen > 180) yBoard = 3;
				else if (yScreen > 120) yBoard = 2;
				else if (yScreen > 60) yBoard = 1;
				// Determine cell color
				if (((xBoard + yBoard) & 1) == 1) begin
					// a.k.a (xBoard + yBoard) % 2 == 1
					color = 1;
				end
			end
		end
	end
	
	assign VGA_HSYNC = hSync;
	assign VGA_VSYNC = vSync;
	assign VGA_RED = color;
	assign VGA_GREEN = color;
	assign VGA_BLUE = color;

endmodule
