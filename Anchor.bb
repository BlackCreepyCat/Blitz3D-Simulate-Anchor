Function UV_Default(UV#[8],Angle#=0.0,FitSize#=0.0)
	UV[0]=0:UV[1]=0
	UV[2]=1:UV[3]=0
	UV[4]=1:UV[5]=1
	UV[6]=0:UV[7]=1
	If Angle>0	UV_Rotate(UV,Angle,FitSize)
End Function

Function UV_Rotate(UV#[8],Angle#,FitRect#=0.0)
	; centre de la rotation
	Local cx#=(UV[0]+UV[2]+UV[4]+UV[6])/4
	Local cy#=(UV[1]+UV[3]+UV[5]+UV[7])/4
	; Distance Handle-Points
	Local r#,dx#,dy#,Ang#
	For n = 0 To 3
		dx#		=	cx-UV[n*2]
		dy#		=	cy-UV[n*2+1]
		r		=	Sqr(dx*dx+dy*dy)
		Ang#	=	ATan2(dy,dx)+Angle
		UV[n*2]	=	cx+r*Cos(ang)
		UV[n*2+1]=	cy+r*Sin(ang)
	Next
	If FitRect>0	UV_Stretch(UV,FitRect)
End Function


; Stretch UV 0.0->Size Centered on C {centerx,centery}
Function UV_Stretch(UV#[8],Size#=1.0)
	; centre des UVs
	Local cx#=(UV[0]+UV[2]+UV[4]+UV[6])/4
	Local cy#=(UV[1]+UV[3]+UV[5]+UV[7])/4
	Local Ang#=0.0,coef#=1.0,r#=0.0
	Local du#=Abs(cx-UV[0])
	Local dv#=Abs(cy-UV[1])
	Size=Abs(Size/2)
	If du<dv
		If dv>Size	coef#=Size/dv
	Else
		If du>Size	coef#=Size/du
	EndIf
	For n = 0 To 3
		du		=	cx-UV[n*2+0]
		dv		=	cy-UV[n*2+1]
		r		=	Sqr(du*du+dv*dv)*coef
		Ang		=	ATan2(dv,du)
		UV[n*2]	=	cx+r*Cos(ang)
		UV[n*2+1]=	cy+r*Sin(ang)
	Next
End Function

; Fit Uv
Function UV_FitSquare(UV#[8],U#,V#,W#,H#)
	If Abs(w)<.00001 Return
	If Abs(h)<.00001 Return

	Local x0#=UV[0],x1#=UV[0]
	Local y0#=UV[1],y1#=UV[1]

	For n = 1 To 3
		If UV[n*2+0]<x0 x0=UV[n*2+0]
		If UV[n*2+0]>x1 x1=UV[n*2+0]
		If UV[n*2+1]<y0 y0=UV[n*2+1]
		If UV[n*2+1]>y1 y1=UV[n*2+1]
	Next

	Local dw#=W/(x1-x0)
	Local dh#=H/(y1-y0)

	For n = 0 To 3
		UV[n*2+0]=	u+(UV[n*2+0]-x0)*dw
		UV[n*2+1]=	v+(UV[n*2+1]-y0)*dh
	Next
End Function

; ReScale UV
Function UV_Scale(UV#[8],Scale#=1.0)
	; centre des UVs
	Local cx#=(UV[0]+UV[2]+UV[4]+UV[6])/4
	Local cy#=(UV[1]+UV[3]+UV[5]+UV[7])/4
	Local Ang#=0.0,coef#=1.0,r#=0.0,du#=0.0,dv#=0.0
	For n = 0 To 3
		du		=	cx-UV[n*2+0]
		dv		=	cy-UV[n*2+1]
		r		=	Sqr(du*du+dv*dv)*Scale
		Ang		=	ATan2(dv,du)
		UV[n*2]	=	cx+r*Cos(ang)
		UV[n*2+1]=	cy+r*Sin(ang)
	Next
End Function

Function UV_Draw(UV#[8],X#=0,Y#=0,S#=1,Mode%=0,PlotSize%=3)
	If Mode=0
		For n = 0 To 3
		Line UV[n*2+0]*s+x,UV[n*2+1]*s+y,UV[(n*2+2) Mod(8)]*s+x,UV[(n*2+3) Mod(8)]*s+y
		Next
	ElseIf Mode=1
		For n = 0 To 3
		Rect UV[n*2+0]*s-PlotSize/2+x,UV[n*2+1]*s-PlotSize/2+y,PlotSize,PlotSize,0
		Next
	EndIf
End Function

Function UV_Text(UV#[8],X#=0,Y#=0,CenterX%=0,CenterY%=0)
	For n=0 To 3	:Text x,y+n*15,UV[n*2]+" "+UV[n*2+1],CenterX,CenterY:Next
End Function







Graphics 800,600,0,2
SetBuffer BackBuffer()

Local UV#[8] : UV_Default(UV)
Local UV1#[8] : UV_Default(UV1)
UVX#=256+50*2
UVY#=100
UVW#=256
UVH#=256
PICKS#=5
Pick=1

MoveMouse UVX+128,UVY+64

FitSize#=0.25
Repeat :ang=(ang+1) Mod(360)
	Cls

		UV_Default(UV)
		Color 150,150,150:Text 050,005,"Default":UV_Text(UV,050,020)

		Color 050,050,050:Rect 50,100,256,256,1
		Color 128,128,128:Rect 50+128-128*FitSize,100+128-128*FitSize,256*FitSize,256*FitSize,0
		Color 255,255,000:UV_Draw(UV,50,100,256)
		Text 10,410,"UV Default"

		UV_Rotate(UV,ang,FitSize)
		Color 255,128,000:UV_Draw(UV,50,100,256,1,7)
		Color 255,128,000:UV_Draw(UV,50,100,256)
		Text 10,425,"UV Rotate + Fit Size"

		UV_Scale(UV,2)
		Color 000,128,255:UV_Draw(UV,50,100,256,1,7)
		Color 000,128,255:UV_Draw(UV,50,100,256)
		Text 10,440,"UV Scaled"
		Color 150,150,150:Text 200,005,"Final" :UV_Text(UV,200,020)

		; Custom UV
		msx=MouseXSpeed():msy=MouseYSpeed()
		If Pick>0 And Pick<5
			u#=Float(UVX)/256:w#=Float(UVW)/256
			v#=Float(UVY)/256:h#=Float(UVH)/256
			UV_FitSquare(UV1,u,v,w,h)
		EndIf
		If MouseDown(1)
			mx=MouseX():my=MouseY()
			If pick=False
				; Rotator
				If RectsOverlap(UVX-PICKS*2-PICKS*5,UVY-PICKS*2-PICKS*5,PICKS*4+1,PICKS*4+1,mx,my,1,1)
					Pick=5
				; corner Up-Lf
				ElseIf RectsOverlap(UVX-PICKS,UVY-PICKS,PICKS*2+1,PICKS*2+1,mx,my,1,1)
					Pick=1
				; corner Up-Rt
				ElseIf RectsOverlap(UVX+UVW-PICKS,UVY-PICKS,PICKS*2+1,PICKS*2+1,mx,my,1,1)
					Pick=2

				; corner Dn-Lf
				ElseIf RectsOverlap(UVX-PICKS,UVY+UVH-PICKS,PICKS*2+1,PICKS*2+1,mx,my,1,1)
					Pick=3
				; corner Dn-Rt
				ElseIf RectsOverlap(UVX+UVW-PICKS,UVY+UVH-PICKS,PICKS*2+1,PICKS*2+1,mx,my,1,1)
					Pick=4
				EndIf
			EndIf
			Select Pick
				Case 5
					UV_Rotate(UV1,msx-msy)
				Case 1
					UVW=UVW+UVX-mx:UVX=mx
					UVH=UVH+UVY-my:UVY=my
				Case 2
					UVW=mx-UVX
					UVH=UVH+UVY-my:UVY=my
				Case 3
					UVW=UVW+UVX-mx:UVX=mx
					UVH=my-UVY
				Case 4
					UVW=mx-UVX
					UVH=my-UVY
					
			End Select
		Else
			Pick=False
		EndIf
		Color 0,255,0
			Rect UVX-PICKS,UVY-PICKS,PICKS*2,PICKS*2,0
			Rect UVX+UVW-PICKS,UVY-PICKS,PICKS*2,PICKS*2,0
			Rect UVX-PICKS,UVY+UVH-PICKS,PICKS*2,PICKS*2,0
			Rect UVX+UVW-PICKS,UVY+UVH-PICKS,PICKS*2,PICKS*2,0
		Color 255,128,0
			Rect UVX-PICKS*2-PICKS*5,UVY-PICKS*2-PICKS*5,PICKS*4,PICKS*4,0
		Color 255,000,255:UV_Draw(UV1,0,0,256,1,7)
		Color 255,000,000:UV_Draw(UV1,0,0,256)
		Color 150,150,150:UV_Text(UV1,256+50*2,10)
	Flip
Until KeyHit(1)
End
