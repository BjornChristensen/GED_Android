package com.example.ged;

import android.graphics.Canvas;

public class Camera {
    V3 O=new V3(0,0,0);
    V3 i=new V3(1,0,0);
    V3 j=new V3(0,1,0);
    V3 k=new V3(0,0,1);

    V3 E=new V3(0,0,0);  // position of camera eye - in world coordinates
    V3 D=new V3(1,0,0);  // 1st-camera-basis vector - Depth direction - in world coordinates
    V3 U=new V3(0,1,0);  // 2nd-camera-basis vector - Up direction - in world coordinates
    V3 R=new V3(0,0,1);  // 3rd-camera-basis vector - Right hand direction - in world coordinates
    double z=4;          // distance from E to projectionplane, in D direction, in camera coordinates
    
    S2 screen;           // 2D projectionplane

    public Camera(int sx, int sy, int ox, int oy){
        screen=new S2(sx,sy, ox,oy);
    }

    V2 project(V3 P){
        V3 EP=P.sub(E);             // P relative to Eye in world coordinates
        double d=D.dot(EP);         // Transform P to Camera coordinates
        double u=U.dot(EP);
        double r=R.dot(EP);
        if (d<=0) return null;       // clip points behind the camera
        double rm=r*z/d;            // Projected coordinates
        double um=u*z/d;
        return new V2(rm,um);
    }

    void moveTo(V3 p){                     // Move camera Eye to point p
//        E=p.copy();
        E=p;
    }

    void translate(V3 v){                  // Translate camera Eye by vector v
        E=E.add(v);
    }

    // Set D-axis points at point p, U-axis points up and R-axis points right.
    void focus(V3 p){
        D=p.sub(E).unit();
        R=D.cross(k).unit();
        U=R.cross(D).unit();
    }

    void zoom(double zoomFactor){
        z=zoomFactor;
    }

    // rotate around U-axis
    void yaw(int v){ yaw(2*Math.PI*v/360.0);  }   // v in deg
    void yaw(double phi){                         // phi in rad
      M3 Su=new M3(   0, -U.z,  U.y,      // Eberly S matrix
                    U.z,    0, -U.x,
                   -U.y,  U.x,    0);
      M3 Ru=M3.I.add(Su.mul(Math.sin(phi))).add(Su.mul(Su).mul((1-Math.cos(phi))));
      D=Ru.mul(D);
      // U=Ru.mul(U);  Not changed
      R=Ru.mul(R);
    }

    // rotate around D-axis
    void roll(int v){ roll(2*Math.PI*v/360.0);  }   // v in deg
    void roll(double phi){                          // phi in rad
      M3 Su=new M3(     0, -D.z,  D.y,      // Eberly S matrix
                    D.z,      0, -D.x,
                   -D.y,  D.x,     0);
      M3 Ru=M3.I.add(Su.mul(Math.sin(phi))).add(Su.mul(Su).mul((1-Math.cos(phi))));
      // D=Ru.mul(D); Not changed
      U=Ru.mul(U);
      R=Ru.mul(R);
    }
    
    // rotate around R-axis
    void pitch(int v){ pitch(2*Math.PI*v/360.0);  }   // v in deg
    void pitch(double phi){                           // phi in rad
      M3 Su=new M3(   0, -R.z,  R.y,                // Eberly S matrix
                    R.z,    0, -R.x,
                   -R.y,  R.x,   0);
      M3 Ru=M3.I.add(Su.mul(Math.sin(phi))).add(Su.mul(Su).mul((1-Math.cos(phi))));
      D=Ru.mul(D);
      U=Ru.mul(U);
      // R=Ru.mul(R); Not changed
    }

    public void drawAxis(Canvas g, double length){
        // World origo and basis vectors in world coordinates
        drawLine(g, O, i.mul(length));
        drawLine(g, O, j.mul(length));
        drawLine(g, O, k.mul(length));
        drawString(g, i.mul(length+0.1), "x");
        drawString(g, j.mul(length+0.1), "y");
        drawString(g, k.mul(length+0.1), "z");
    }

    public void drawPoint(Canvas g, V3 p){
        V2 pp=project(p); if (pp==null) return;         // p was clipped
        screen.drawPoint(g, pp);
    }

    public void drawLine(Canvas g, V3 p1, V3 p2){
        V2 pp1=project(p1); if (pp1==null) return;       // p1 was clipped
        V2 pp2=project(p2); if (pp2==null) return;       // p2 was clipped
        screen.drawLine(g, pp1, pp2);
    }

    public void drawString(Canvas g, V3 p, String str){
        V2 pp=project(p); if (pp==null) return;         // p was clipped
        screen.drawString(g, pp, str);
    }

    void setStrokeWidth(float w){
        screen.setStrokeWidth(w);
    }
} // class Camera

