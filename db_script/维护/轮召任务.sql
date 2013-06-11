select a.logical_addr,a.gp_char,a.gp_sn,a.task_id,a.protocol_no,a.sys_object,a.startup_flag,
a.time_interval,a.base_time_gw,a.sendup_cycle_gw,a.sendup_unit_gw,a.ext_cnt_gw,
b.start_time_master,b.end_time_master,b.exec_cycle_master,b.exec_unit_master,b.AFN,c.gp_addr,nvl(c.port,'1') port,nvl(e.btl,'6') btl
from r_term_task a,r_task b, c_gp c , c_terminal d,c_ps e
where a.task_id = b.task_id
and a.protocol_no = b.protocol_no
and c.term_id = d.term_id
and a.logical_addr = d.logical_addr
and a.gp_sn = c.gp_sn
and a.gp_char = c.gp_char
and c.gp_id = e.gp_id(+)
and b.task_type = '2'--//主站轮召
and a.startup_flag = '1'
--and b.exec_unit_master =//周期单位
--and a.TIME_INTERVAL =
