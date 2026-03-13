import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Package, Plus, BookOpen, Settings } from 'lucide-react';
import './BottomNav.css';

const navItems = [
  { path: '/', icon: LayoutDashboard, label: 'Home' },
  { path: '/inventory', icon: Package, label: 'Inventory' },
  { path: '/billing', icon: Plus, label: 'Bill', isFab: true },
  { path: '/khata', icon: BookOpen, label: 'Khata' },
  { path: '/settings', icon: Settings, label: 'Settings' },
];

export default function BottomNav() {
  return (
    <nav className="bottom-nav">
      <div className="bottom-nav-inner">
        {navItems.map(item => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `nav-item ${isActive ? 'nav-item-active' : ''} ${item.isFab ? 'nav-item-fab' : ''}`
            }
          >
            {item.isFab ? (
              <div className="fab-btn">
                <item.icon size={24} strokeWidth={2.5} />
              </div>
            ) : (
              <>
                <item.icon size={20} strokeWidth={1.8} />
                <span className="nav-label">{item.label}</span>
              </>
            )}
          </NavLink>
        ))}
      </div>
    </nav>
  );
}
