package testCases;

import io.RW;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import memoryindex.IQueryStrategyQT;
import memoryindex.IVisitorQT;
import memoryindex.QuadTree;

import org.junit.BeforeClass;
import org.junit.Test;

import spatialindex.Point;
import spatialindex.Region;

public class TestQuadTree {

	static ArrayList<Point> points = new ArrayList<Point>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		int num = 16;
		for (int i = 0; i < num; i ++) {
			points.add(new Point(new double[]{i, i}));
		}
	}

	@Test
	public void testInsertWithPath() {
		QuadTree root = new QuadTree(4, new Region(new double[] {0, 0}, new double[] {16, 16}));
		for (int i = 0; i < points.size(); i ++) {
			ArrayList<QuadTree> path = new ArrayList<QuadTree>();
			if (!root.insert(points.get(i), new Id(i), path)) {
				System.out.println(i + " err!");
			} else {
				System.out.println(i + ": " + path.size());
//				for (int j = 0; j < path.size(); j ++) {					
//					System.out.println(path.get(j));
//				}
			}
		}
//		System.out.println(root);
	}
	
	@Test
	public void testRegionSubdivision() {
		double[] lCoords = new double[]{1, 2}, hCoords = new double[]{3, 4};
		Region region = new Region(lCoords, hCoords);
		Region[] regions = QuadTree.subDivide(region);
		for (int i = 0; i < 4; i ++) {
			System.out.println(regions[i]);
		}
	}
	
	@Test
	public void testInsert() {
		QuadTree root = new QuadTree(4, new Region(new double[] {0, 0}, new double[] {16, 16}));
		for (int i = 0; i < points.size(); i ++) {			
			if (!root.insert(points.get(i), new Id(i))) {
				System.out.println(i + " err!");
			} else {
//				System.out.println(i + " inserted!");
			}
		}
//		System.out.println(root);
	}
	
	@Test
	public void testRemove() {
		QuadTree root = new QuadTree(4, new Region(new double[] {0, 0}, new double[] {16, 16}));
		for (int i = 0; i < points.size(); i ++) {			
			if (!root.insert(points.get(i), new Id(i))) {
				System.out.println(i + " err!");
			}
		}
		root.remove(points.get(6));
		root.remove(points.get(5));
		root.remove(points.get(4));
		root.remove(points.get(3));
		root.remove(points.get(2));
		root.remove(points.get(1));
//		root.insert(2, points.get(2));
//		System.out.println(root);
	}
	
	@Test 
	public void testRange() {
		QuadTree root = new QuadTree(4, new Region(new double[] {0, 0}, new double[] {16, 16}));
		for (int i = 0; i < points.size(); i ++) {			
			if (!root.insert(points.get(i), new Id(i))) {
				System.out.println(i + " err!");
			} else {
//				System.out.println(i + " inserted!");
			}
		}
		RangeQueryStrategy qs = new RangeQueryStrategy(new Region(points.get(2), points.get(5)));
		QuadTree.queryStrategy(root, qs);
		ArrayList<RW> res = qs.getResult();
		for (int i = 0; i < res.size(); i ++) {
			System.out.println(((Id)res.get(i)).getId());
		}
	}
	
	@Test
	public void testKNN() {
		QuadTree root = new QuadTree(4, new Region(new double[] {0, 0}, new double[] {16, 16}));
		for (int i = 0; i < points.size(); i ++) {			
			if (!root.insert(points.get(i), new Id(i))) {
				System.out.println(i + " err!");
			} else {
//				System.out.println(i + " inserted!");
			}
		}
		Point query = new Point(new double[] {8, 8});
		root.nearestNeighborQuery(3, query, new NNVisitor());
		System.out.println("----");
		root.nearestNeighborQuery(5, query, new NNVisitor());
	}
	
	class NNVisitor implements IVisitorQT {

		@Override
		public void visitPoint(Point p) {
			System.out.println(p);
		}
		
	}
	
	class RangeQueryStrategy implements IQueryStrategyQT {

		private ArrayList<QuadTree> toVisit = new ArrayList<QuadTree>();
		private ArrayList<RW> 	results = new ArrayList<RW>();
		private Region query = null;
		
		
		public RangeQueryStrategy(Region query) {
			super();
			this.query = query;
		}

		public ArrayList<RW> getResult() {
			return results;
		}

		@Override
		public void getNextEntry(QuadTree n, QuadTree[] next,
				boolean[] hasNext) {
			// TODO Auto-generated method stub
		
			ArrayList<Point> points = n.getPoints();
			if (points != null) {
				ArrayList<RW> ids = n.getValues();
				for (int i = 0; i < points.size(); i ++) {
					Point p = points.get(i);
					if (query.contains(p)) {
						results.add(ids.get(i));
					}
				}
			} else {
				QuadTree[] chTrees = n.getChTrees();
				for (int i = 0; i < n.getDim(); i ++) {
					if (chTrees[i].getBoundary().intersects(query)) {
						toVisit.add(chTrees[i]);
					}
				}
			}
			
			if (!toVisit.isEmpty()) {
				next[0] = toVisit.remove(0);
				hasNext[0] = true;
			} else {
				hasNext[0] = false;
			}
		}
	}
	
	class Id implements RW {
		int id;

		public Id(int id) {
			this.id = id;
		}
		
		@Override
		public void read(DataInputStream ds) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void write(DataOutputStream ds) {
			// TODO Auto-generated method stub
			
		}
		
		public int getId() {
			return id;
		}
	}

}
