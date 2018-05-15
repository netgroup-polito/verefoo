/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Optimize.Handle;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Z3Exception;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verifoo.rest.common.AutoContext;
import it.polito.verifoo.rest.common.ConditionExtractor;
import it.polito.verifoo.rest.jaxb.BandwidthConstraints;
import it.polito.verifoo.rest.jaxb.BandwidthConstraints.BandwidthMetrics;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.Core;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.components.Quattro;

/**
 * Model for a network, encompasses routing and wiring
 *
 *
 */
public class Network extends Core {

	Context ctx;
	NetContext nctx;
	List<BoolExpr> constraints;
	public List<NetworkObject> elements;

	HashMap<BoolExpr, Tuple<Integer, String>> softConstraints;

	public Network(Context ctx, Object[]... args) {
		super(ctx, args);
	}

	@Override
	protected void init(Context ctx, Object[]... args) {
		this.ctx = ctx;
		this.nctx = (NetContext) args[0][0];
		constraints = new ArrayList<BoolExpr>();
		elements = new ArrayList<NetworkObject>();
		softConstraints = new HashMap<>();

	}

	/**
	 * Composes the network linking the configured network objects
	 *
	 * @param elements
	 */
	public void attach(NetworkObject... elements) {
		for (NetworkObject el : elements)
			this.elements.add(el);
	}

	@Override
	protected void addConstraints(Optimize solver) {
		try {

			BoolExpr[] constr = new BoolExpr[constraints.size()];
			//System.out.println("Nr of network hard constraint " + constraints.stream().distinct().count());
			/*constraints.forEach(c -> {
				System.out.println("======adding hard constraint " + c);
			});*/
			solver.Add(constraints.toArray(constr));
			//System.out.println("Nr of network soft constraint " + softConstraints.keySet().stream().distinct().count());
			for (Entry<BoolExpr, Tuple<Integer, String>> entry : softConstraints.entrySet()) {
				// String key = entry.getKey();
				//System.out.println("======adding soft for " + entry.getKey() + "\n with value " + entry.getValue()._1 + ". Node is " + entry.getValue()._2 + " ====== ");
				Handle temp = solver.AssertSoft((entry.getKey()), entry.getValue()._1, "opt");
				nctx.handles.put("handle_" + entry.getValue()._2, temp);

			}
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Specify host to address mapping. Handles the case in which we have more
	 * than one address for a node
	 * 
	 * @param addrmap
	 */
	public void setAddressMappings(ArrayList<Tuple<NetworkObject, ArrayList<DatatypeExpr>>> addrmap) {
		// Set address mapping for nodes.
		for (Tuple<NetworkObject, ArrayList<DatatypeExpr>> entry : addrmap) {
			NetworkObject node = entry._1;
			List<DatatypeExpr> addr = entry._2;
			Expr a_0 = ctx.mkConst(node + "_address_mapping_a_0", nctx.address);
			ArrayList<BoolExpr> or_clause = new ArrayList<BoolExpr>();
			// Constraint 1 addrToNode(foreach ad in addr) = node
			for (DatatypeExpr ad : addr) {
				constraints.add(ctx.mkEq(nctx.addrToNode.apply(ad), node.getZ3Node()));
				or_clause.add(ctx.mkEq(a_0, ad));
				// System.out.println("Constraints mapping: " +
				// (ctx.mkEq(nctx.addrToNode.apply(ad), node.getZ3Node())));
			}
			BoolExpr[] orClause = new BoolExpr[or_clause.size()];

			// Constraint 2nodeHasAddr(node, a_0) == Or(foreach ad in addr (a_0
			// == ad))
			// Note we need the iff here to make sure that we set nodeHasAddr to
			// false
			// for other addresses.
			constraints.add(ctx.mkForall(new Expr[] { a_0 },
					ctx.mkEq(ctx.mkOr(or_clause.toArray(orClause)), nctx.nodeHasAddr.apply(node.getZ3Node(), a_0)), 1,
					null, null, null, null));

		}
		// System.out.println("Constraints mapping: " + constraints);
	}

	/**
	 * Don't forward packets addressed to node
	 * 
	 * @param node
	 */
	public void saneSend(NetworkObject node) {
		Expr n_0 = ctx.mkConst(node + "_saneSend_n_0", nctx.node);
		Expr p_0 = ctx.mkConst(node + "_saneSend_p_0", nctx.packet);
		// IntExpr t_0 = ctx.mkIntConst(node+"_saneSend_t_0");
		// Constant: node
		// Constraint send(node, n_0, p, t_0) -> !nodeHasAddr(node, p.dest) &
		// !nodeHasAddr(n_0, p.src) & p.src!=null & p.dst!=null
		// timeless
		constraints
				.add(ctx.mkForall(new Expr[] { n_0, p_0 },
						ctx.mkImplies((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0), ctx.mkAnd(// ctx.mkNot((BoolExpr)nctx.nodeHasAddr.apply(
																										// node.getZ3Node(),
																										// nctx.pf.get("dest").apply(p_0))),
																										// ctx.mkNot((BoolExpr)nctx.nodeHasAddr.apply(
																										// n_0,
																										// nctx.pf.get("src").apply(p_0))),
								ctx.mkNot(ctx.mkEq(this.nctx.pf.get("src").apply(p_0), this.nctx.am.get("null"))),
								ctx.mkNot(ctx.mkEq(this.nctx.pf.get("dest").apply(p_0), this.nctx.am.get("null"))))),
						1, null, null, null, null));
	}

	/**
	 * Node sends all traffic through gateway
	 * 
	 * @param node
	 * @param gateway
	 */
	public void setGateway(NetworkObject node, NetworkObject gateway) {
		// SetGateway(self, node, gateway): All packets from node are sent
		// through gateway
		Expr n_0 = ctx.mkConst(node + "_gateway_n_0", nctx.node);
		Expr p_0 = ctx.mkConst(node + "_gateway_p_0", nctx.packet);
		// IntExpr t_0 = ctx.mkIntConst(node+"_gateway_t_0");

		// Constraint send(node, n_0, p_0, t_0) -> n_0 = gateway
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 }, ctx
				.mkImplies((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0), ctx.mkEq(n_0, gateway.getZ3Node())),
				1, null, null, null, null));

		// constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
		// ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0, node.getZ3Node(), p_0,
		// t_0),
		// ctx.mkEq(n_0,gateway.getZ3Node())),1,null,null,null,null));
	}

	/**
	 * Assigns a specific routing table to a network object. Routing entries in
	 * the form: address -> node
	 * 
	 * @param node
	 * @param routing_table
	 */
	public void routingTable(NetworkObject node, ArrayList<Tuple<DatatypeExpr, NetworkObject>> routing_table) {
		compositionPolicy(node, routing_table);
	}

	public void routingTable2(NetworkObject node,
			ArrayList<RoutingTable> rta) {
		compositionPolicy2(node, rta);
	}

	/**
	 * Composition policies steer packets between middleboxes.
	 * 
	 * @param node
	 * @param policy
	 */
	public void compositionPolicy(NetworkObject node, ArrayList<Tuple<DatatypeExpr, NetworkObject>> policy) {
		// Policy is of the form predicate -> node
		Expr p_0 = ctx.mkConst(node + "_composition_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(node + "_composition_n_0", nctx.node);
		// Expr t_0 = ctx.mkIntConst(node+"_composition_t_0");

		HashMap<String, ArrayList<BoolExpr>> collected = new HashMap<String, ArrayList<BoolExpr>>();
		HashMap<String, NetworkObject> node_dict = new HashMap<String, NetworkObject>();
		BoolExpr predicates;
		for (int y = 0; y < policy.size(); y++) {
			Tuple<DatatypeExpr, NetworkObject> tp = policy.get(y);
			if (collected.containsKey("" + tp._2))
				collected.get("" + tp._2).add(nctx.destAddrPredicate(p_0, tp._1));
			else {
				ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
				alb.add(nctx.destAddrPredicate(p_0, tp._1));
				collected.put("" + tp._2, alb);

			}
			node_dict.put("" + tp._2, tp._2);

		}
		// System.out.println("collected: " + collected);

		// Constraintforeach rtAddr,rtNode in rt( send(node, n_0, p_0, t_0) &&
		// Or(foreach rtAddr in rt destAddrPredicate(p_0,rtAddr)) -> n_0 ==
		// rtNode )
		for (Map.Entry<String, ArrayList<BoolExpr>> entry : collected.entrySet()) {
			BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
			predicates = ctx.mkOr(entry.getValue().toArray(pred));

			constraints
					.add(ctx.mkForall(new Expr[] { n_0, p_0 },
							ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0), predicates),
									ctx.mkEq(n_0, node_dict.get(entry.getKey()).getZ3Node())),
							1, null, null, null, null));
		}

	}

	/**
	 * Routing entries are in the form: address -> node. Also allows packet to
	 * be sent to another box for further processing
	 * 
	 * @param node
	 * @param routing_table
	 * @param shunt_node
	 */
	public void routingTableShunt(NetworkObject node, ArrayList<Tuple<DatatypeExpr, NetworkObject>> routing_table,
			NetworkObject shunt_node) {
		compositionPolicyShunt(node, routing_table, shunt_node);
	}

	/**
	 * Composition policies steer packets between middleboxes.Policy is in the
	 * form: predicate -> node
	 * 
	 * @param node
	 * @param routing_table
	 * @param shunt_node
	 */

	public void compositionPolicy2(NetworkObject node,
			ArrayList<RoutingTable> rta) {
		// Policy is of the form predicate -> node
		Expr p_0 = ctx.mkConst(node + "_composition_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(node + "_composition_n_0", nctx.node);

		HashMap<String, ArrayList<BoolExpr>> collected = new HashMap<String, ArrayList<BoolExpr>>();
		HashMap<String, NetworkObject> node_dict = new HashMap<String, NetworkObject>();
		HashMap<String, HashMap<String, Tuple<Integer, BoolExpr>>> latency = new HashMap<>();

		BoolExpr predicates;
		for (int y = 0; y < rta.size(); y++) {
			RoutingTable tp = rta.get(y);
			Tuple<Integer, BoolExpr> temp = new Tuple<>(tp.latency, tp.condition);
			if (collected.containsKey("" + tp.nextHop)) {
				if (!collected.get("" + tp.nextHop).contains(nctx.destAddrPredicate(p_0, tp.ip)))
					collected.get("" + tp.nextHop).add(nctx.destAddrPredicate(p_0, tp.ip));
				latency.get("" + tp.nextHop).put("" + tp.latency + "_" + tp.condition, temp);
			} else {
				HashMap<String, Tuple<Integer, BoolExpr>> lists = new HashMap<>();
				ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
				if (!alb.contains(nctx.destAddrPredicate(p_0, tp.ip)))
					alb.add(nctx.destAddrPredicate(p_0, tp.ip));
				collected.put("" + tp.nextHop, alb);
				// list contains [latency with boolean] to the actual tuple
				// like 1_y1 with (1,y1)
				lists.put("" + tp.latency + "_" + tp.condition, temp);
				// latency contains element to be forwarded to with the lists
				latency.put("" + tp.nextHop, lists);
			}
			node_dict.put("" + tp.nextHop, tp.nextHop);

		}

		// Constraint foreach rtAddr,rtNode in rt( send(node, n_0, p_0, t_0) &&
		// Or(foreach rtAddr in rt destAddrPredicate(p_0,rtAddr)) -> n_0 ==
		// rtNode )
		
		for (Map.Entry<String, ArrayList<BoolExpr>> entry : collected.entrySet()) {
			BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
			HashMap<String, Tuple<Integer, BoolExpr>> sett = latency.get(entry.getKey());
			///System.out.println("-------for " + entry.getKey() + " we have " + sett.size() + " for the node " + node);
			predicates = ctx.mkOr(entry.getValue().toArray(pred));

			for (Entry<String, Tuple<Integer, BoolExpr>> temp : sett.entrySet()) {
				//constraints.add(ctx.mkEq(nctx.y1, ctx.mkFalse()));
				BoolExpr forTheKey = temp.getValue()._2;
				Integer latency_val = temp.getValue()._1;
				BoolExpr mkImplies = ctx.mkImplies( forTheKey,
								ctx.mkForall(new Expr[] { n_0,p_0 },
								ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
														ctx.mkAnd(ctx.mkEq(n_0,node_dict.get(entry.getKey()).getZ3Node()))
														)
								,1, null, null, null, null))
						;
				constraints.add(mkImplies);
				///System.out.println("\n SO for " + node +"w="+latency_val + "\n" + mkImplies );
			}
		}
	}
	
	public void routingOptimization(NetworkObject node,
			ArrayList<RoutingTable> rta) {
		// Policy is of the form predicate -> node
		Expr p_0 = ctx.mkConst(node + "_composition_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(node + "_composition_n_0", nctx.node);

		HashMap<String, ArrayList<BoolExpr>> collected = new HashMap<String, ArrayList<BoolExpr>>();
		HashMap<String, NetworkObject> node_dict = new HashMap<String, NetworkObject>();
		HashMap<String, HashMap<String, Tuple<Integer, BoolExpr>>> latency = new HashMap<>();
		System.out.println("==========NEW ROUTING TABLE==========");
		BoolExpr predicates;
		for (int y = 0; y < rta.size(); y++) {
			RoutingTable tp = rta.get(y);
			System.out.println(tp.condition + " lat: "+ tp.latency);
			Tuple<Integer, BoolExpr> temp = new Tuple<>(tp.latency, tp.condition);
			if (collected.containsKey("" + tp.nextHop)) {
				if (!collected.get("" + tp.nextHop).contains(nctx.destAddrPredicate(p_0, tp.ip)))
					collected.get("" + tp.nextHop).add(nctx.destAddrPredicate(p_0, tp.ip));
				latency.get("" + tp.nextHop).put("" + tp.latency + "_" + tp.condition, temp);
			} else {
				HashMap<String, Tuple<Integer, BoolExpr>> lists = new HashMap<>();
				ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
				if (!alb.contains(nctx.destAddrPredicate(p_0, tp.ip)))
					alb.add(nctx.destAddrPredicate(p_0, tp.ip));
				collected.put("" + tp.nextHop, alb);
				// list contains [latency with boolean] to the actual tuple
				// like 1_y1 with (1,y1)
				lists.put("" + tp.latency + "_" + tp.condition, temp);
				// latency contains element to be forwarded to with the lists
				latency.put("" + tp.nextHop, lists);
			}
			node_dict.put("" + tp.nextHop, tp.nextHop);
		}

		System.out.println("Collected: " + collected);
		System.out.println("Latency: " + latency);
		System.out.println("Node_dict: " + node_dict);
		// Constraint foreach rtAddr,rtNode in rt( send(node, n_0, p_0, t_0) &&
		// Or(foreach rtAddr in rt destAddrPredicate(p_0,rtAddr)) -> n_0 ==
		// rtNode )
		ArrayList<IntExpr> routes = new ArrayList<>();
		for (Map.Entry<String, ArrayList<BoolExpr>> entry : collected.entrySet()) {
			BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
			HashMap<String, Tuple<Integer, BoolExpr>> sett = latency.get(entry.getKey());
			System.out.println("In the node " + node + " we have " + sett.size() + " possible scenario the for next hop: " + entry.getKey() +"(can be deployed on " + sett.size() + " different combination of hosts)" );
			System.out.println("Entry: " + entry);
			predicates = ctx.mkOr(entry.getValue().toArray(pred));
			System.out.println("Predicates: " + predicates);
			for (Entry<String, Tuple<Integer, BoolExpr>> temp : sett.entrySet()) {
				//constraints.add(ctx.mkEq(nctx.y1, ctx.mkFalse()));
				System.out.println("Temp: " + temp.getValue());
				BoolExpr forTheKey = temp.getValue()._2;
				Integer latency_val = temp.getValue()._1;
				BoolExpr initial = ctx.mkForall(new Expr[] { n_0,p_0 },
				ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
										ctx.mkAnd(ctx.mkEq(n_0,node_dict.get(entry.getKey()).getZ3Node()))
										)
				,1, null, null, null, null);
				System.out.println(initial + " => " + forTheKey);
				BoolExpr mkImplies = ctx.mkImplies( initial,forTheKey);
				softConstraints.put(mkImplies, new Tuple<Integer, String>(latency_val, node + "_" + entry.getKey()));
				constraints.add(initial);
				///System.out.println("\n SO for " + node +"w="+latency_val + "\n" + mkImplies );
				routes.add(nctx.bool_to_int(mkImplies));
			}
			IntExpr list[] = new IntExpr[routes.size()];
			System.out.println("Route List: " + routes);
			constraints.add(ctx.mkEq(ctx.mkAdd(routes.toArray(list)), ctx.mkInt(1)));
		}
	}

	public void routingOptimizationSG(NetworkObject node, ArrayList<RoutingTable> rta, List<BandwidthMetrics> bConstraints) {
		// Policy is of the form predicate -> node
				Expr p_0 = ctx.mkConst(node + "_composition_p_0", nctx.packet);
				Expr n_0 = ctx.mkConst(node + "_composition_n_0", nctx.node);

				HashMap<String, ArrayList<BoolExpr>> collected = new HashMap<String, ArrayList<BoolExpr>>();
				HashMap<String, ArrayList<BoolExpr>> collectedWithOptional = new HashMap<String, ArrayList<BoolExpr>>();
				HashMap<String, ArrayList<BoolExpr>> collectedWithoutOptional = new HashMap<String, ArrayList<BoolExpr>>();
				HashMap<String, NetworkObject> node_dict = new HashMap<String, NetworkObject>();
				HashMap<String, HashMap<String, Tuple<Integer, BoolExpr>>> latency = new HashMap<>();
				System.out.println("==========NEW ROUTING TABLE for " + node.getZ3Node() + "==========");
				BoolExpr predicates = null;
				//Collect some information in order to build the conditions in the next step
				ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
				for (int y = 0; y < rta.size(); y++) {
					RoutingTable tp = rta.get(y);
					//System.out.println(tp.condition + " lat: "+ tp.latency);
					Tuple<Integer, BoolExpr> temp = new Tuple<>(tp.latency, tp.condition);
					if (collected.containsKey("" + tp.nextHop)) {
						if (!collected.get("" + tp.nextHop).contains(nctx.destAddrPredicate(p_0, tp.ip)))
							collected.get("" + tp.nextHop).add(nctx.destAddrPredicate(p_0, tp.ip));
						latency.get("" + tp.nextHop).put("" + tp.latency + "_" + tp.condition, temp);
					} else {
						HashMap<String, Tuple<Integer, BoolExpr>> lists = new HashMap<>();
						if (!alb.contains(nctx.destAddrPredicate(p_0, tp.ip)))
							alb.add(nctx.destAddrPredicate(p_0, tp.ip));
						collected.put("" + tp.nextHop, alb);
						if(tp.condition.toString().contains("(not "))
							collectedWithOptional.put("" + tp.nextHop, alb);
						else{
							collectedWithoutOptional.put("" + tp.nextHop, alb);
						}
						// list contains [latency with boolean] to the actual tuple
						// like 1_y1 with (1,y1)
						lists.put("" + tp.latency + "_" + tp.condition, temp);
						// latency contains element to be forwarded to with the lists
						latency.put("" + tp.nextHop, lists);
					}
					node_dict.put("" + tp.nextHop, tp.nextHop);
				}

				//System.out.println("Collected: " + collected);
				//System.out.println("Collected Without Optional: " + collectedWithoutOptional);
				//System.out.println("Latency: " + latency);
				//System.out.println("Node_dict: " + node_dict);

				List<BoolExpr> possibleNextHops = new ArrayList<>();
				Map<String, List<BoolExpr>> initials = new HashMap<>();
				List<BoolExpr> nextHops = new ArrayList<>();
				List<BoolExpr> nextHopsWithOptional = new ArrayList<>();
				List<BoolExpr> nextHopsWithoutOptional = new ArrayList<>();
				for(String s : collected.keySet()){
					nextHops.add(ctx.mkEq(n_0,node_dict.get(s).getZ3Node()));
				}
				for(String s : collectedWithOptional.keySet()){
					nextHopsWithOptional.add(ctx.mkEq(n_0,node_dict.get(s).getZ3Node()));
				}
				for(String s : collectedWithoutOptional.keySet()){
					nextHopsWithoutOptional.add(ctx.mkEq(n_0,node_dict.get(s).getZ3Node()));
				}
				System.out.println("Next Hops: " + nextHops);
				System.out.println("Next Hops With Optional: " + nextHopsWithOptional);
				System.out.println("Next Hops Without Optional: " + nextHopsWithoutOptional);
				for (Map.Entry<String, ArrayList<BoolExpr>> entry : collected.entrySet()) {
					BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
					HashMap<String, Tuple<Integer, BoolExpr>> sett = latency.get(entry.getKey());
					//System.out.println("For (" + node + "-->" +entry.getKey()+") there are " + sett.size() + " possible scenarios of deployment");
					//System.out.println("Entry: " + entry);
					predicates = ctx.mkOr(entry.getValue().toArray(pred));
					//System.out.println("Predicates: " + predicates);
					BoolExpr[] tmp = new BoolExpr[nextHops.size()];
					BoolExpr initial = ctx.mkForall(new Expr[] { n_0,p_0 },
													ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
																			ctx.mkOr(nextHops.toArray(tmp))
																	)
													,1, null, null, null, null);
					BoolExpr initialWithOptional = null;
					if(nextHopsWithOptional.size() > 0){
						BoolExpr[] tmpWithOptional = new BoolExpr[nextHopsWithOptional.size()];
						initialWithOptional = ctx.mkForall(new Expr[] { n_0,p_0 },
													ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
																			ctx.mkOr(nextHopsWithOptional.toArray(tmpWithOptional))
																	)
													,1, null, null, null, null);
					}
					BoolExpr initialWithoutOptional = null;
					if(nextHopsWithoutOptional.size() > 0){
						BoolExpr[] tmpWithoutOptional = new BoolExpr[nextHopsWithoutOptional.size()];
						initialWithoutOptional = ctx.mkForall(new Expr[] { n_0,p_0 },
													ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
																			ctx.mkOr(nextHopsWithoutOptional.toArray(tmpWithoutOptional))
																	)
													,1, null, null, null, null);
					}
					//BoolExpr initial = ctx.mkEq(n_0,node_dict.get(entry.getKey()).getZ3Node());
					if(!initials.containsKey(entry.getKey())){
						initials.put(entry.getKey(), new ArrayList<>());
					}
					initials.get(entry.getKey()).add(initial);
					ArrayList<IntExpr> routes = new ArrayList<>();
					Integer minLatency = null;
					if(bConstraints != null && bConstraints.size() > 0){
						minLatency = bConstraints.stream().filter(b -> b.getDst().equals(entry.getKey())).map(b -> b.getReqLatency()).findFirst().orElse(null);
					}
					List<BoolExpr> forTheKeys = new ArrayList<>();
					boolean optional = false;
					for (Entry<String, Tuple<Integer, BoolExpr>> temp : sett.entrySet()) {
						//System.out.println("Temp: " + temp.getValue());
						//System.out.println("Key: " + temp.getKey());
						BoolExpr forTheKey = temp.getValue()._2;
						Integer latency_val = -temp.getValue()._1;
						assert(latency_val <= 0);
						softConstraints.put(forTheKey, new Tuple<Integer, String>(latency_val, node + "_" + entry.getKey()));
						forTheKeys.add(forTheKey);
						if(forTheKey.toString().contains("(not ")){
							optional = true;
						}
						if(minLatency != null){
							//System.out.println("Latency Constraint: " + ctx.mkLe(ctx.mkMul(ctx.mkInt(-latency_val),nctx.bool_to_int(forTheKey)), ctx.mkMul(ctx.mkInt(minLatency),nctx.bool_to_int(forTheKey))));
							constraints.add(ctx.mkLe(ctx.mkMul(ctx.mkInt(-latency_val),nctx.bool_to_int(forTheKey)), ctx.mkMul(ctx.mkInt(minLatency),nctx.bool_to_int(forTheKey))));
						}
					}
					BoolExpr tmpKeys[] = new BoolExpr[forTheKeys.size()];
					BoolExpr implication = ctx.mkOr(forTheKeys.toArray(tmpKeys));
					BoolExpr mkImplies;
					/*if(optional){
						System.out.println("Condition with optional: " + initialWithOptional + " => " + implication);
						mkImplies = ctx.mkImplies( initialWithOptional,implication);
					}else{
						System.out.println("Condition without optional: " + initialWithoutOptional + " => " + implication);
						mkImplies = ctx.mkImplies( initialWithoutOptional,implication);*/
						System.out.println("Condition plain: " + initial + " => " + implication);
						mkImplies = ctx.mkImplies( initial,implication);
					//}
					
					routes.add(nctx.bool_to_int(mkImplies));
					IntExpr list[] = new IntExpr[routes.size()];
					//System.out.println("Route List: " + routes);
					possibleNextHops.add(ctx.mkEq(ctx.mkAdd(routes.toArray(list)), ctx.mkInt(1)));
					
				}
				if(initials.size() > 0){
					BoolExpr initList[] = new BoolExpr[initials.size()];
					//System.out.println("Initial List: " + initials.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList()));
					
					constraints.add(ctx.mkOr(initials.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList()).toArray(initList)));
				}
				
				if(possibleNextHops.size() > 0){
					
					BoolExpr l[] = new BoolExpr[possibleNextHops.size()];
					//System.out.println("All Conditions: " + possibleNextHop.toArray(list).length);
					//System.out.println("AND Conditions: " + possibleNextHops);
					constraints.add(ctx.mkAnd(possibleNextHops.toArray(l)));
				}
	}
	
	public void routingOptimizationSGOptional(NetworkObject node, ArrayList<RoutingTable> rta, List<BandwidthMetrics> bConstraints, AutoContext autoctx) {
		// Policy is of the form predicate -> node
		Expr p_0 = ctx.mkConst(node + "_composition_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(node + "_composition_n_0", nctx.node);

		HashMap<String, ArrayList<BoolExpr>> collected = new HashMap<String, ArrayList<BoolExpr>>();
		HashMap<String, ArrayList<BoolExpr>> collectedWithOptional = new HashMap<String, ArrayList<BoolExpr>>();
		HashMap<String, ArrayList<BoolExpr>> collectedWithoutOptional = new HashMap<String, ArrayList<BoolExpr>>();
		HashMap<String, NetworkObject> node_dict = new HashMap<String, NetworkObject>();
		HashMap<String, HashMap<String, Tuple<Integer, BoolExpr>>> latency = new HashMap<>();
		//System.out.println("==========NEW ROUTING TABLE for " + node.getZ3Node() + "==========");
		BoolExpr predicates = null;
		//Collect some information in order to build the conditions in the next step
		ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
		for (int y = 0; y < rta.size(); y++) {
			RoutingTable tp = rta.get(y);
			//System.out.println(tp.condition + " lat: "+ tp.latency);
			Tuple<Integer, BoolExpr> temp = new Tuple<>(tp.latency, tp.condition);
			if (collected.containsKey("" + tp.nextHop)) {
				if (!collected.get("" + tp.nextHop).contains(nctx.destAddrPredicate(p_0, tp.ip)))
					collected.get("" + tp.nextHop).add(nctx.destAddrPredicate(p_0, tp.ip));
				latency.get("" + tp.nextHop).put("" + tp.latency + "_" + tp.condition, temp);
			} else {
				HashMap<String, Tuple<Integer, BoolExpr>> lists = new HashMap<>();
				if (!alb.contains(nctx.destAddrPredicate(p_0, tp.ip)))
					alb.add(nctx.destAddrPredicate(p_0, tp.ip));
				collected.put("" + tp.nextHop, alb);
				if(autoctx.networkObjectIsOptional(tp.nextHop)){
					collectedWithOptional.put("" + tp.nextHop, alb);
					lists.put("!" + tp.latency + "_" + tp.condition, temp);
				}
				else{
					collectedWithoutOptional.put("" + tp.nextHop, alb);
					// list contains [latency with boolean] to the actual tuple
					// like 1_y1 with (1,y1)
					lists.put("" + tp.latency + "_" + tp.condition, temp);
				}
				
				// latency contains element to be forwarded to with the lists
				latency.put("" + tp.nextHop, lists);
			}
			node_dict.put("" + tp.nextHop, tp.nextHop);
		}

		//System.out.println("Collected: " + collected);
		//System.out.println("Collected With Optional: " + collectedWithOptional);
		//System.out.println("Collected Without Optional: " + collectedWithoutOptional);
		//System.out.println("Latency: " + latency);
		//System.out.println("Node_dict: " + node_dict);

		List<BoolExpr> possibleNextHops = new ArrayList<>();
		Map<String, List<BoolExpr>> initials = new HashMap<>();
		List<BoolExpr> nextHops = new ArrayList<>();
		List<BoolExpr> nextHopsWithOptional = new ArrayList<>();
		List<BoolExpr> nextHopsWithoutOptional = new ArrayList<>();
		List<NetworkObject> optionalInBetween = null;
		for(String s : collected.keySet()){
			nextHops.add(ctx.mkEq(n_0,node_dict.get(s).getZ3Node()));
		}
		for(String s : collectedWithOptional.keySet()){
			nextHopsWithOptional.add(ctx.mkEq(n_0,node_dict.get(s).getZ3Node()));
		}
		for(String s : collectedWithoutOptional.keySet()){
			
			optionalInBetween = autoctx.hasOptionalNodes(node, node_dict.get(s));
			//System.out.println("Searching optional between " + node + " and " + node_dict.get(s) + ". Found: " + optionalInBetween);
			nextHopsWithoutOptional.add(ctx.mkEq(n_0,node_dict.get(s).getZ3Node()));
		}
		//System.out.println("Next Hops: " + nextHops);
		//System.out.println("Next Hops With Optional: " + nextHopsWithOptional);
		//System.out.println("Next Hops Without Optional: " + nextHopsWithoutOptional);
		for (Map.Entry<String, ArrayList<BoolExpr>> entry : collected.entrySet()) {
			BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
			HashMap<String, Tuple<Integer, BoolExpr>> sett = latency.get(entry.getKey());
			//System.out.println("For (" + node + "-->" +entry.getKey()+") there are " + sett.size() + " possible scenarios of deployment");
			//System.out.println("Entry: " + entry);
			predicates = ctx.mkOr(entry.getValue().toArray(pred));
			//System.out.println("Predicates: " + predicates);
			BoolExpr choosenInitial = null;
			BoolExpr initialWithOptional = null;
			BoolExpr initialWithoutOptional = null;
			if(nextHopsWithOptional.size() > 0){
				if(optionalInBetween != null && optionalInBetween.size() > 0){
					//System.out.println("Checking " + entry.getKey() + " in " + optionalInBetween);
					List<BoolExpr> optionalInBetweenTmp = optionalInBetween.stream()
																			//.filter(no -> !no.toString().equals(entry.getKey()))
																			.map(no -> no.isUsed())
																			.collect(Collectors.toList());
					if(optionalInBetweenTmp.size() > 0){
						//System.out.println("Left from before: " + optionalInBetweenTmp);
						BoolExpr tmp2[] = new BoolExpr[optionalInBetweenTmp.size()];
						BoolExpr[] tmpWithOptional = new BoolExpr[nextHopsWithOptional.size()];
						initialWithOptional = ctx.mkForall(new Expr[] { n_0,p_0 },
													ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
																			ctx.mkAnd(ctx.mkOr(nextHopsWithOptional.toArray(tmpWithOptional))    , ctx.mkAnd(optionalInBetweenTmp.toArray(tmp2)))
																	)
													,1, null, null, null, null);
						//System.out.println("Special with optional conditions: " + initialWithOptional);
					}else{
						BoolExpr[] tmpWithOptional = new BoolExpr[nextHopsWithOptional.size()];
						initialWithOptional = ctx.mkForall(new Expr[] { n_0,p_0 },
								ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
														ctx.mkOr(nextHopsWithOptional.toArray(tmpWithOptional))
												)
								,1, null, null, null, null);
						//System.out.println("Optional conditions: " + initialWithOptional);
					}
				}else{
					BoolExpr[] tmpWithOptional = new BoolExpr[nextHopsWithOptional.size()];
					initialWithOptional = ctx.mkForall(new Expr[] { n_0,p_0 },
							ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
													ctx.mkOr(nextHopsWithOptional.toArray(tmpWithOptional))
											)
							,1, null, null, null, null);
					//System.out.println("Optional conditions: " + initialWithOptional);
				}
			}
			if(nextHopsWithoutOptional.size() > 0){
				if(optionalInBetween != null && optionalInBetween.size() > 0){
					//System.out.println("Checking " + entry.getKey() + " in " + optionalInBetween);
					List<BoolExpr> optionalInBetweenTmp = optionalInBetween.stream()
																			.filter(no -> !no.toString().equals(entry.getKey()))
																			.map(no -> ctx.mkNot(no.isUsed()))
																			.collect(Collectors.toList());
					if(optionalInBetweenTmp.size() > 0){
						//System.out.println("Left from before: " + optionalInBetweenTmp);
						BoolExpr tmp2[] = new BoolExpr[optionalInBetweenTmp.size()];
						BoolExpr[] tmpWithoutOptional = new BoolExpr[nextHopsWithoutOptional.size()];
						initialWithoutOptional = ctx.mkForall(new Expr[] { n_0,p_0 },
													ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
																			ctx.mkAnd(ctx.mkOr(nextHopsWithoutOptional.toArray(tmpWithoutOptional))  , ctx.mkAnd(optionalInBetweenTmp.toArray(tmp2)))
																	)
													,1, null, null, null, null);
						//System.out.println("Special without optional conditions: " + initialWithoutOptional);
					}
				}else{
					BoolExpr[] tmpWithoutOptional = new BoolExpr[nextHopsWithoutOptional.size()];
					initialWithoutOptional = ctx.mkForall(new Expr[] { n_0,p_0 },
												ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
																		ctx.mkOr(nextHopsWithoutOptional.toArray(tmpWithoutOptional))
																)
												,1, null, null, null, null);
					//System.out.println("No optional conditions: " + initialWithoutOptional);
				}
			}
			
			ArrayList<IntExpr> routes = new ArrayList<>();
			Integer minLatency = null;
			if(bConstraints != null && bConstraints.size() > 0){
				minLatency = bConstraints.stream().filter(b -> b.getDst().equals(entry.getKey())).map(b -> b.getReqLatency()).findFirst().orElse(null);
			}
			List<BoolExpr> forTheKeys = new ArrayList<>();
			boolean optional = false;
			for (Entry<String, Tuple<Integer, BoolExpr>> temp : sett.entrySet()) {
				//System.out.println("Temp: " + temp.getValue());
				//System.out.println("Key: " + temp.getKey());
				BoolExpr forTheKey = temp.getValue()._2;
				Integer latency_val = -temp.getValue()._1;
				assert(latency_val <= 0);
				softConstraints.put(forTheKey, new Tuple<Integer, String>(latency_val, node + "_" + entry.getKey()));
				forTheKeys.add(forTheKey);
				if(temp.getKey().indexOf("!") == 0){
					optional = true;
				}
				if(minLatency != null){
					//System.out.println("Latency Constraint: " + ctx.mkLe(ctx.mkMul(ctx.mkInt(-latency_val),nctx.bool_to_int(forTheKey)), ctx.mkMul(ctx.mkInt(minLatency),nctx.bool_to_int(forTheKey))));
					constraints.add(ctx.mkLe(ctx.mkMul(ctx.mkInt(-latency_val),nctx.bool_to_int(forTheKey)), ctx.mkMul(ctx.mkInt(minLatency),nctx.bool_to_int(forTheKey))));
				}
			}
			BoolExpr tmpKeys[] = new BoolExpr[forTheKeys.size()];
			BoolExpr implication = ctx.mkOr(forTheKeys.toArray(tmpKeys));
			BoolExpr mkImplies;
			if(optional){
				mkImplies = ctx.mkImplies( initialWithOptional,implication);
				choosenInitial = initialWithOptional;
				//System.out.println("Condition with optional: " + choosenInitial + " => " + implication);
			}else{
				mkImplies = ctx.mkImplies( initialWithoutOptional,implication);
				choosenInitial = initialWithoutOptional;
				//System.out.println("Condition without optional: " + choosenInitial + " => " + implication);
			}
			//BoolExpr initial = ctx.mkEq(n_0,node_dict.get(entry.getKey()).getZ3Node());
			if(!initials.containsKey(entry.getKey())){
				initials.put(entry.getKey(), new ArrayList<>());
			}
			initials.get(entry.getKey()).add(choosenInitial);
			routes.add(nctx.bool_to_int(mkImplies));
			IntExpr list[] = new IntExpr[routes.size()];
			//System.out.println("Route List: " + routes);
			possibleNextHops.add(ctx.mkEq(ctx.mkAdd(routes.toArray(list)), ctx.mkInt(1)));
			
		}
		if(initials.size() > 0){
			List<BoolExpr> tmpList = initials.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
			BoolExpr initList[] = new BoolExpr[tmpList.size()];
			//System.out.println("Initial List: " + tmpList);
			
			constraints.add(ctx.mkOr(tmpList.toArray(initList)));
		}
		
		if(possibleNextHops.size() > 0){
			BoolExpr l[] = new BoolExpr[possibleNextHops.size()];
			//System.out.println("All Conditions: " + possibleNextHop.toArray(list).length);
			//System.out.println("AND Conditions: " + possibleNextHops);
			constraints.add(ctx.mkAnd(possibleNextHops.toArray(l)));
		}
	}

	public void routingOptimizationSG2(NetworkObject node, ArrayList<RoutingTable> rta, List<BandwidthMetrics> bConstraints, Map<String, List<DatatypeExpr>> destinations) {
		// Policy is of the form predicate -> node
		Expr p_0 = ctx.mkConst(node + "_composition_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(node + "_composition_n_0", nctx.node);

		HashMap<String, ArrayList<BoolExpr>> collected = new HashMap<String, ArrayList<BoolExpr>>();
		HashMap<String, NetworkObject> node_dict = new HashMap<String, NetworkObject>();
		HashMap<String, HashMap<String, Tuple<Integer, BoolExpr>>> latency = new HashMap<>();
		//System.out.println("==========NEW ROUTING TABLE for " + node + "==========");
		BoolExpr predicates = null;
		//Collect some information in order to build the conditions in the next step
		
		for (int y = 0; y < rta.size(); y++) {
			RoutingTable tp = rta.get(y);
			//System.out.println(tp.condition + " lat: "+ tp.latency);
			Tuple<Integer, BoolExpr> temp = new Tuple<>(tp.latency, tp.condition);
			if (collected.containsKey("" + tp.nextHop)) {
				if (!collected.get("" + tp.nextHop).contains(nctx.destAddrPredicate(p_0, tp.ip))){
					collected.get("" + tp.nextHop).add(nctx.destAddrPredicate(p_0, tp.ip));
				}
				latency.get("" + tp.nextHop).put("" + tp.latency + "_" + tp.condition, temp);
			} else {
				HashMap<String, Tuple<Integer, BoolExpr>> lists = new HashMap<>();
				ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
				alb.add(nctx.destAddrPredicate(p_0, tp.ip));
				collected.put("" + tp.nextHop, alb);
				// list contains [latency with boolean] to the actual tuple
				// like 1_y1 with (1,y1)
				lists.put("" + tp.latency + "_" + tp.condition, temp);
				// latency contains element to be forwarded to with the lists
				latency.put("" + tp.nextHop, lists);
			}
			node_dict.put("" + tp.nextHop, tp.nextHop);
		}
		//System.out.println("Collected: " + collected);
		//System.out.println("Latency: " + latency);
		//System.out.println("Node_dict: " + node_dict);

		List<BoolExpr> possibleNextHops = new ArrayList<>();
		Map<String, List<BoolExpr>> initials = new HashMap<>();
		Map<String, BoolExpr> nextHops = new HashMap<>();
		for(String s : collected.keySet()){
			nextHops.put(s, ctx.mkEq(n_0,node_dict.get(s).getZ3Node()));
		}
		//System.out.println("Next Hops: " + nextHops);
		for (Map.Entry<String, ArrayList<BoolExpr>> entry : collected.entrySet()) {
			BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
			HashMap<String, Tuple<Integer, BoolExpr>> sett = latency.get(entry.getKey());
			//System.out.println("For (" + node + "-->" +entry.getKey()+") there are " + sett.size() + " possible scenarios of deployment");
			//System.out.println("Entry: " + entry);
			predicates = ctx.mkOr(entry.getValue().toArray(pred));
			//System.out.println("Predicates: " + predicates);
			BoolExpr initial, initialInternal = null;
			if(!initials.containsKey(entry.getKey())){
				initials.put(entry.getKey(), new ArrayList<>());
			}
			List<DatatypeExpr> currDest = destinations.get(entry.getKey());
			if(currDest != null){
				BoolExpr[] predInternal = new BoolExpr[currDest.size()];
				BoolExpr predicatesInternal = ctx.mkOr(currDest.stream().map(d -> nctx.destAddrPredicate(p_0, d)).collect(Collectors.toList()).toArray(predInternal));
				//System.out.println("PredicatesInternal: " + predicatesInternal);
				initialInternal = ctx.mkForall(new Expr[] { n_0,p_0 },
										ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0), predicatesInternal),
																nextHops.get(entry.getKey())
														)
										,1, null, null, null, null);
				//System.out.println("InitialInternal: " + initialInternal);
				initials.get(entry.getKey()).add(initialInternal);
			}
			BoolExpr[] tmp = new BoolExpr[nextHops.size()];
			List<BoolExpr> allNextHops = nextHops.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
			initial = ctx.mkForall(new Expr[] { n_0,p_0 },
											ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0),predicates),
																	ctx.mkOr(allNextHops.toArray(tmp))
															)
											,1, null, null, null, null);
			//System.out.println("Initial: " + initial);
			initials.get(entry.getKey()).add(initial);
			ArrayList<IntExpr> routes = new ArrayList<>();
			Integer minLatency = null;
			if(bConstraints.size() > 0){
				minLatency = bConstraints.stream().filter(b -> b.getDst().equals(entry.getKey())).map(b -> b.getReqLatency()).findFirst().orElse(null);
			}
			for (Entry<String, Tuple<Integer, BoolExpr>> temp : sett.entrySet()) {
				//System.out.println("Temp: " + temp.getValue());
				BoolExpr forTheKey = temp.getValue()._2;
				Integer latency_val = -temp.getValue()._1;
				assert(latency_val <= 0);
				BoolExpr mkImplies;
				/*if(initialInternal != null){
					System.out.println(ctx.mkOr(initialInternal, initial) + " => " + forTheKey);
					mkImplies = ctx.mkImplies( ctx.mkOr(initialInternal, initial) , forTheKey );
				}else{
					System.out.println(initial + " => " + forTheKey);
					mkImplies = ctx.mkImplies(initial , forTheKey );
				}*/
				mkImplies = ctx.mkImplies(initial , forTheKey );
				softConstraints.put(forTheKey, new Tuple<Integer, String>(latency_val, node + "_" + entry.getKey()));
				routes.add(nctx.bool_to_int(mkImplies));
				
				if(minLatency != null){
					//System.out.println("Latency Constraint: " + ctx.mkLe(ctx.mkMul(ctx.mkInt(-latency_val),nctx.bool_to_int(forTheKey)), ctx.mkMul(ctx.mkInt(minLatency),nctx.bool_to_int(forTheKey))));
					constraints.add(ctx.mkLe(ctx.mkMul(ctx.mkInt(-latency_val),nctx.bool_to_int(forTheKey)), ctx.mkMul(ctx.mkInt(minLatency),nctx.bool_to_int(forTheKey))));
				}
			}
			IntExpr list[] = new IntExpr[routes.size()];
			//System.out.println("Route List: " + routes);
			possibleNextHops.add(ctx.mkEq(ctx.mkAdd(routes.toArray(list)), ctx.mkInt(1)));
			
		}
		if(initials.size() > 0){
			BoolExpr initList[] = new BoolExpr[initials.size()];
			//System.out.println("Initial List: " + initials.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList()));
			
			constraints.add(ctx.mkOr(initials.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList()).toArray(initList)));
		}
		
		if(possibleNextHops.size() > 0){
			
			BoolExpr l[] = new BoolExpr[possibleNextHops.size()];
			//System.out.println("All Conditions: " + possibleNextHop.toArray(list).length);
			//System.out.println("AND Conditions: " + possibleNextHops);
			constraints.add(ctx.mkAnd(possibleNextHops.toArray(l)));
		}
	}
	
	
	public void internalRoutingOptimizationSG(NetworkObject src, List<DatatypeExpr> destinations, NetworkObject nextHop) {
		// Policy is of the form predicate -> node
		Expr p_0 = ctx.mkConst(src + "_composition_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(src + "_composition_n_0", nctx.node); 
		//System.out.println("Next Hops: " + nextHops);
		//System.out.println("For (" + node + "-->" +entry.getKey()+") there are " + sett.size() + " possible scenarios of deployment");
		//System.out.println("Entry: " + entry);
		List<BoolExpr> predicates = new ArrayList<>();
		for(DatatypeExpr d : destinations){
			predicates.add(nctx.destAddrPredicate(p_0, d)); //(= (dest node4_composition_p_0) nodeB)
		}
		BoolExpr[] tmp = new BoolExpr[destinations.size()];
		//System.out.println("Predicates: " + predicates);
		
		BoolExpr initial = ctx.mkForall(new Expr[] { n_0,p_0 },
										ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(src.getZ3Node(), n_0, p_0),ctx.mkOr(predicates.toArray(tmp))),
														ctx.mkEq(n_0, nextHop.getZ3Node())
														)
										,1, null, null, null, null);
		System.out.println("Rule: " + initial);
		constraints.add(initial);
	}
	
	
	
	public void compositionPolicyShunt(NetworkObject node, ArrayList<Tuple<DatatypeExpr, NetworkObject>> routing_table,
			NetworkObject shunt_node) {
		Expr p_0 = ctx.mkConst(node + "_composition_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(node + "_composition_n_0", nctx.node);
		// Expr t_0 = ctx.mkIntConst(node+"_composition_t_0");

		HashMap<String, ArrayList<BoolExpr>> collected = new HashMap<String, ArrayList<BoolExpr>>();
		HashMap<String, NetworkObject> node_dict = new HashMap<String, NetworkObject>();
		BoolExpr predicates;
		for (int y = 0; y < routing_table.size(); y++) {
			Tuple<DatatypeExpr, NetworkObject> tp = routing_table.get(y);
			if (collected.containsKey("" + tp._2))
				collected.get("" + tp._2).add(nctx.destAddrPredicate(p_0, tp._1));
			else {
				ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
				alb.add(nctx.destAddrPredicate(p_0, tp._1));
				collected.put("" + tp._2, alb);
			}
			node_dict.put("" + tp._2, tp._2);
		}

		// Constraintforeach rtAddr,rtNode in rt( send(node, n_0, p_0, t_0) &&
		// Or(foreach rtAddr in rt destAddrPredicate(p_0,rtAddr)) -> n_0 ==
		// rtNode )
		for (Map.Entry<String, ArrayList<BoolExpr>> entry : collected.entrySet()) {
			BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
			predicates = ctx.mkOr(entry.getValue().toArray(pred));

			constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 },
					ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0), predicates),
							ctx.mkOr(ctx.mkEq(n_0, node_dict.get(entry.getKey()).getZ3Node()),
									ctx.mkEq(n_0, shunt_node.getZ3Node()))),
					1, null, null, null, null));
		}

	}

	// public void SimpleIsolation (NetworkObject node, ArrayList<DatatypeExpr>
	// addresses){
	// Expr p = ctx.mkConst(node+"_s_p", nctx.packet);
	// Expr n = ctx.mkConst(node+"_s_n", nctx.node);
	// IntExpr t = ctx.mkInt(node+"_s_t");
	//
	// BoolExpr[] a_pred= new BoolExpr[addresses.size()];
	// for(int y=0;y<addresses.size();y++){
	// DatatypeExpr de = addresses.get(y);
	// a_pred[y] = ctx.mkOr(ctx.mkEq(nctx.pf.get("src").apply(p),
	// de),ctx.mkEq(nctx.pf.get("dest").apply(p), de));
	// }
	//
	// constraints.add(
	// ctx.mkForall(new Expr[]{p, n, t},
	// ctx.mkImplies((BoolExpr)nctx.recv.apply(n, node.getZ3Node(), p, t),
	// ctx.mkOr(a_pred)),1,null,null,null,null));
	// constraints.add(
	// ctx.mkForall(new Expr[]{p, n, t},
	// ctx.mkImplies((BoolExpr)nctx.send.apply(node.getZ3Node(), n, p, t),
	// ctx.mkOr(a_pred)),1,null,null,null,null));
	// }

	/**
	 * Set isolation constraints on a node. Doesn't need to be set but useful
	 * when interfering policies are in play.
	 * 
	 * @param node
	 * @param adjacencies
	 *
	 */
	public void SetIsolationConstraint(NetworkObject node, ArrayList<NetworkObject> adjacencies) {

		Expr n_0 = ctx.mkConst(node + "_isolation_n_0", nctx.node);
		Expr p_0 = ctx.mkConst(node + "_isolation_p_0", nctx.packet);
		// IntExpr t_0 = ctx.mkInt(node+"_isolation_t_0");

		BoolExpr[] adj = new BoolExpr[adjacencies.size()];
		for (int y = 0; y < adjacencies.size(); y++) {
			NetworkObject no = adjacencies.get(y);
			adj[y] = ctx.mkEq(n_0, no.getZ3Node());
		}
		BoolExpr clause = ctx.mkOr(adj);

		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 },
				ctx.mkImplies((BoolExpr) nctx.send.apply(node.getZ3Node(), n_0, p_0), clause), 1, null, null, null,
				null));
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 },
				ctx.mkImplies((BoolExpr) nctx.recv.apply(n_0, node.getZ3Node(), p_0), clause), 1, null, null, null,
				null));
		
		
	}

	/**
	 * Return all currently attached endhosts
	 * 
	 * @return NetworkObject
	 */
	public List<String> EndHosts() {
		List<String> att_nos = new ArrayList<String>();
		for (NetworkObject el : elements) {
			if (el.isEndHost) {
				// System.out.println("el: "+el);
				att_nos.add(el.getZ3Node().toString());
			}
		}
		return att_nos;
	}
}
