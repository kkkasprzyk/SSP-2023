from mininet.topo import Topo


class MyTopo(Topo):
    def build(self):
        # Dodaj przełącznik
        switch1 = self.addSwitch('s1')
        switch2 = self.addSwitch('s2')
        switch3 = self.addSwitch('s3')
        switch4 = self.addSwitch('s4')

        # Dodaj dwa hosty
        host1 = self.addHost('Host1')
        host2 = self.addHost('Host2')

        # Połącz hosty z przełącznikiem
        self.addLink(switch2, switch1)
        self.addLink(switch3, switch2)
        self.addLink(switch3, switch4)
        self.addLink(switch4, switch1)
        self.addLink(host1, switch2)
        self.addLink(host2, switch4)


# Uruchom topologię w terminalu: sudo mn --custom topo.py --topo=mytopo
topos = {'mytopo': (lambda: MyTopo())}