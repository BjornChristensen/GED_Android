package com.example.ged;

import android.graphics.Canvas;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

// A WaveFrontElement represents a 3D image in vector graphics.
// It is made up of an array of vertices (points in 3D space)
// and an array of edges (line segments between two points)
// Data is loaded from an .obj Wave Front file.
// The file format is defined here: https://en.wikipedia.org/wiki/Wavefront_.obj_file
// WaveFront files uses a rihgt handed coordinate system, but axis are different fram the camera system:
// Camera coordinate system,                    WaveFront coordinate system
// x-axis points towards the viewer             z-axis points towards the viewer
// y-axis points to the right                   x-axis points to the right
// z-axis points up                             y-axis points up
public class WaveFrontElement {
    V3[] vertices=new V3[0];                    // Virtual world coordinates of points
    Edge[] edges=new Edge[0];                   // Each element is a pair of indexes into vertices

    WaveFrontElement(String filename){
        load(filename);
    }

    V3 center(){
        V3 res=new V3(0,0,0);
        for (V3 v:vertices) res=res.add(v);
        return res.mul(1.0/vertices.length);
    }

    // Draw all edges with a camera
    public void draw(Camera c, Canvas g){   // Graphics changed to Canvas
        for (Edge e: edges) c.drawLine(g, vertices[e.a], vertices[e.b]);
    }

    // Rotates theta radians around an axis given by vector u through point o
    public void rotate(V3 u, double theta){
        u=u.unit();                              // Rotation axis must be unit length
        M3 Su=new M3(    0, -u.z,  u.y,      // rotation around u-vector
                u.z,    0, -u.x,
                -u.y,u.x,    0 );
        M3 Ru=M3.I.add(Su.mul(Math.sin(theta))).add(Su.mul(Su).mul((1-Math.cos(theta)))); // Eq (2.22)
        for (int i=0; i<vertices.length; i++)
            vertices[i]=Ru.mul(vertices[i]);
    }

    // Rotates theta radians around an axis given by vector u through point o
    public void rotate(V3 u,  V3 o, double theta){
        u=u.unit();                              // Rotation axis must be unit length
        M3 Su=new M3(    0, -u.z,  u.y,      // rotation around u-vector
                u.z,    0, -u.x,
                -u.y,u.x,    0 );
        M3 Ru=M3.I.add(Su.mul(Math.sin(theta))).add(Su.mul(Su).mul((1-Math.cos(theta)))); // Eq (2.22)
        for (int i=0; i<vertices.length; i++)
            vertices[i]=Ru.mul(vertices[i].sub(o)).add(o);
    }

    // Translate by vector v
    public void translate(V3 v){
        for (int i=0; i<vertices.length; i++)
            vertices[i]=vertices[i].add(v);
    }

    // Move center of this WaveFrontElement to point p
    public void moveTo(V3 p){
        V3 v=p.sub(center());
        translate(v);
    }

    private void load(String filename) {
        ArrayList<V3> vList =new ArrayList<>();     // Temp list of vertices
        ArrayList<Edge> eList =new ArrayList<>();   // Temp list of edges
        try {
            File file = new File(filename);
            Scanner input=new Scanner(file);
            while (input.hasNextLine()){
                String line=input.nextLine();
                line = line.replaceAll("  ", " ");
                String[] parts=line.split(" ");

                // A Vertex is a 3D point
                if (parts[0].equals("v")){
                    double x=Double.parseDouble(parts[1]);  // x-coordinate in WaveFront coordinate system
                    double y=Double.parseDouble(parts[2]);  // y-coordinate in WaveFront coordinate system
                    double z=Double.parseDouble(parts[3]);  // z-coordinate in WaveFront coordinate system
                    vList.add(new V3(-z,-x,y));             // coordinates are toggled to match camera system
                    continue;
                }

                // "l" is a polyLine, "f" is a face (polygon=closed polyline)
                // Example og a face: f 1/1 2/2 3/3
                // The indices are 1-based (first index is 1)
                if (parts[0].equals("l")||parts[0].equals("f")){
                    int n =parts.length-1;                                   // index 0 in parts is "l"
                    for (int i = 1; i<n; i++){                               // for each line segment
                        int a=Integer.parseInt(parts[i].split("/")[0])-1;    // each line segment is defined by two indices a and b to vertices in list
                        int b=Integer.parseInt(parts[i+1].split("/")[0])-1;  // The vertices list is 0-based, so subtract 1
                        eList.add(new Edge(a,b));
                    }
                    if (parts[0].equals("f")){                               // close the polygon
                        int a=Integer.parseInt(parts[n].split("/")[0])-1;    // last vertex
                        int b=Integer.parseInt(parts[1].split("/")[0])-1;    // back to first vertex
                        eList.add(new Edge(a,b));
                    }
                    continue;
                }
            }
            vertices=vList.toArray(vertices);   // Store vertices in an array
            edges=eList.toArray(edges);         // Store edges in an array
        } catch (FileNotFoundException e){
//            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            Log.d("VR_Helicopter", e.toString());
            e.printStackTrace();
        }
    } // load()

    // An Edge object represents a line segment of a WaveFront object
    // It has two integers a and b that defines the endpoints.
    // a and b are indices in the array of vertices
    class Edge {
        int a,b;
        Edge(int a, int b){ this.a=a; this.b=b; }
        public String toString() { return "("+a+","+b+")"; }
    } // class Edge

    public static void main(String[] args) {
        WaveFrontElement cube=new WaveFrontElement("src/WaveFrontFiles/cube.obj");
        for (V3 v: cube.vertices) System.out.println(v);
        for (Edge e: cube.edges) System.out.println(e);
    }
}
