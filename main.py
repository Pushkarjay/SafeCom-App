from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.gridlayout import GridLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.textinput import TextInput
from kivy.uix.spinner import Spinner
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.popup import Popup
from kivy.uix.scrollview import ScrollView
from kivy.uix.widget import Widget
from kivy.core.window import Window
from kivy.utils import get_color_from_hex
from kivy.clock import Clock
from kivy.graphics import Color, Rectangle
import sqlite3
from datetime import datetime
import json

# Set window size for desktop testing
Window.size = (360, 640)  # Typical phone size

def hex_color(hex_string):
    """Convert hex color string to Kivy color tuple"""
    if isinstance(hex_string, (list, tuple)):
        return hex_string
    return get_color_from_hex(hex_string)

# Enhanced modern color scheme with gradients and shadows
PRIMARY_COLOR = '#0A2647'  # Deep Navy Blue
SECONDARY_COLOR = '#144272'  # Rich Blue
ACCENT_COLOR = '#205295'  # Bright Blue
HIGHLIGHT_COLOR = '#2C74B3'  # Light Blue
BACKGROUND_COLOR = '#FFFFFF'  # Pure White
TEXT_COLOR = '#1A1A1A'  # Almost Black
SUBTITLE_COLOR = '#666666'  # Medium Grey
ACTIVE_TAB_COLOR = '#205295'  # Bright Blue
DIVIDER_COLOR = '#E0E0E0'  # Light Grey

# Debug mode for development
DEBUG_MODE = True  # Set to False for production

# Apply global styling
from kivy.core.text import LabelBase
from kivy.config import Config

# Configure window appearance
Config.set('kivy', 'window_shape', 'rounded_rectangle')
Config.set('graphics', 'borderless', '1')

# Configure input behavior
Config.set('input', 'mouse', 'mouse,multitouch_on_demand')

# Set default font properties - use system default font
from kivy.core.text import LabelBase
from kivy.utils import platform

# Kivy will use the default system font

class BaseScreen(Screen):
    def add_debug_back_button(self, layout):
        if DEBUG_MODE:
            back_btn = SecondaryButton(
                text='Debug Nav',
                size_hint=(None, None),
                size=('100dp', '40dp'),
                pos_hint={'right': 1, 'top': 1}
            )
            back_btn.bind(on_press=lambda x: setattr(self.manager, 'current', 'debug_nav'))
            layout.add_widget(back_btn)

class SafecamScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(SafecamScreen, self).__init__(**kwargs)
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title
        title = Label(
            text='SafeCam',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='60dp',
            color=hex_color(TEXT_COLOR)
        )
        
        # Appointment card
        appointment_card = BoxLayout(
            orientation='vertical',
            size_hint_y=None,
            height='100dp',
            padding=15,
            spacing=5
        )
        with appointment_card.canvas.before:
            Color(rgba=hex_color('#F5F5F5'))
            self.card_rect = Rectangle(pos=appointment_card.pos, size=appointment_card.size)
        appointment_card.bind(pos=self._update_rect, size=self._update_rect)
        
        appointment_label = Label(
            text='Camera Status',
            font_size='18sp',
            bold=True,
            size_hint_y=None,
            height='30dp',
            color=hex_color(TEXT_COLOR)
        )
        
        status_label = Label(
            text='All cameras operational',
            font_size='16sp',
            size_hint_y=None,
            height='30dp',
            color=hex_color(SUBTITLE_COLOR)
        )
        
        appointment_card.add_widget(appointment_label)
        appointment_card.add_widget(status_label)
        
        # Action buttons
        monitor_btn = ModernButton(
            text='View Live Feed',
            size_hint=(1, None),
            height='56dp'
        )
        monitor_btn.bind(on_press=self.view_feed)
        
        settings_btn = SecondaryButton(
            text='Camera Settings',
            size_hint=(1, None),
            height='56dp'
        )
        settings_btn.bind(on_press=self.open_settings)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(Widget(size_hint_y=0.1))  # Spacer
        layout.add_widget(appointment_card)
        layout.add_widget(Widget(size_hint_y=0.1))  # Spacer
        layout.add_widget(monitor_btn)
        layout.add_widget(settings_btn)
        layout.add_widget(Widget())  # Flexible spacer
        
        # Add background
        with self.canvas.before:
            Color(rgba=hex_color(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)
        self.add_debug_back_button(layout)
    
    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size
        if hasattr(self, 'card_rect'):
            self.card_rect.pos = instance.pos
            self.card_rect.size = instance.size
    
    def view_feed(self, instance):
        self.show_popup('Opening live camera feed...')
    
    def open_settings(self, instance):
        self.show_popup('Opening camera settings...')
    
    def show_popup(self, message):
        popup = Popup(
            title='Message',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class ServiceScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(ServiceScreen, self).__init__(**kwargs)
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title section
        title = Label(
            text='CCTV Services',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='60dp',
            color=hex_color(TEXT_COLOR)
        )
        
        subtitle = Label(
            text='Choose your service',
            font_size='18sp',
            size_hint=(1, None),
            height='30dp',
            color=hex_color(SUBTITLE_COLOR)
        )
        
        # Service cards grid
        services_grid = GridLayout(
            cols=1,
            spacing=15,
            size_hint_y=None,
            height='320dp'
        )
        
        # Service cards
        services = [
            ('Installation', 'New CCTV system setup', 'install'),
            ('Maintenance', 'Regular system checkup', 'service'),
            ('Upgrade', 'System enhancement', 'upgrade'),
            ('Support', '24/7 technical support', 'support')
        ]
        
        for service_name, desc, action in services:
            service_card = BoxLayout(
                orientation='vertical',
                size_hint_y=None,
                height='70dp',
                padding=15
            )
            with service_card.canvas.before:
                Color(rgba=hex_color('#F5F5F5'))
                Rectangle(pos=service_card.pos, size=service_card.size)
            
            # Service title and description
            title_box = BoxLayout(
                orientation='vertical',
                size_hint_y=None,
                height='60dp'
            )
            
            service_title = Label(
                text=service_name,
                font_size='18sp',
                bold=True,
                color=hex_color(TEXT_COLOR),
                size_hint_y=None,
                height='30dp',
                halign='left'
            )
            service_title.bind(size=service_title.setter('text_size'))
            
            service_desc = Label(
                text=desc,
                font_size='14sp',
                color=hex_color(SUBTITLE_COLOR),
                size_hint_y=None,
                height='20dp',
                halign='left'
            )
            service_desc.bind(size=service_desc.setter('text_size'))
            
            title_box.add_widget(service_title)
            title_box.add_widget(service_desc)
            
            # Add to card
            service_card.add_widget(title_box)
            services_grid.add_widget(service_card)
        
        # Action buttons
        service_btn = ModernButton(
            text='Book Service',
            size_hint=(1, None),
            height='56dp'
        )
        service_btn.bind(on_press=self.book_service)
        
        install_btn = SecondaryButton(
            text='Schedule Installation',
            size_hint=(1, None),
            height='56dp'
        )
        install_btn.bind(on_press=self.new_installation)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(subtitle)
        layout.add_widget(Widget(size_hint_y=0.05))  # Spacer
        layout.add_widget(services_grid)
        layout.add_widget(Widget(size_hint_y=0.05))  # Spacer
        layout.add_widget(service_btn)
        layout.add_widget(install_btn)
        layout.add_widget(Widget())  # Flexible spacer
        
        # Add background
        with self.canvas.before:
            Color(rgba=hex_color(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)
        self.add_debug_back_button(layout)
    
    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size

    def book_service(self, instance):
        self.show_popup('Booking service appointment...')
    
    def new_installation(self, instance):
        self.show_popup('Starting new CCTV installation...')
    
    def show_popup(self, message):
        popup = Popup(
            title='Action',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class TroubleshootingScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(TroubleshootingScreen, self).__init__(**kwargs)
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title section
        title = Label(
            text='Troubleshooting',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='60dp',
            color=hex_color(TEXT_COLOR)
        )
        
        subtitle = Label(
            text='Common CCTV Issues',
            font_size='18sp',
            size_hint=(1, None),
            height='30dp',
        )
        
        # Issues list with cards
        issues_layout = GridLayout(
            cols=1,
            spacing=15,
            size_hint_y=None,
            height='280dp'
        )
        
        issues = [
            ('Camera Not Working', 'Check power and connection status'),
            ('Recording Mode Issues', 'Verify storage and recording settings'),
            ('Network Connection', 'Test network connectivity and IP settings'),
            ('Image Quality', 'Adjust focus and resolution settings')
        ]
        
        for issue_title, issue_desc in issues:
            issue_card = BoxLayout(
                orientation='vertical',
                size_hint_y=None,
                height='60dp',
                padding=10
            )
            with issue_card.canvas.before:
                Color(rgba=hex_color('#F5F5F5'))
                Rectangle(pos=issue_card.pos, size=issue_card.size)
            
            title_box = BoxLayout(
                orientation='vertical',
                size_hint_y=None,
                height='60dp'
            )
            
            title_label = Label(
                text=issue_title,
                font_size='16sp',
                bold=True,
                color=hex_color(TEXT_COLOR),
                size_hint_y=None,
                height='25dp',
                halign='left'
            )
            title_label.bind(size=title_label.setter('text_size'))
            
            desc_label = Label(
                text=issue_desc,
                font_size='14sp',
                color=hex_color(SUBTITLE_COLOR),
                size_hint_y=None,
                height='25dp',
                halign='left'
            )
            desc_label.bind(size=desc_label.setter('text_size'))
            
            title_box.add_widget(title_label)
            title_box.add_widget(desc_label)
            issue_card.add_widget(title_box)
            issues_layout.add_widget(issue_card)
        
        # Help button
        help_btn = ModernButton(
            text='Get Help Now',
            size_hint=(1, None),
            height='56dp'
        )
        help_btn.bind(on_press=self.request_help)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(subtitle)
        layout.add_widget(Widget(size_hint_y=0.05))  # Spacer
        layout.add_widget(issues_layout)
        layout.add_widget(Widget(size_hint_y=0.05))  # Spacer
        layout.add_widget(help_btn)
        layout.add_widget(Widget())  # Flexible spacer
        
        # Add background
        with self.canvas.before:
            Color(rgba=hex_color(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)
        self.add_debug_back_button(layout)
    
    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size
    
    def request_help(self, instance):
        self.show_popup('Connecting to support...')
    
    def show_popup(self, message):
        popup = Popup(
            title='Message',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class ClientAppointmentScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(ClientAppointmentScreen, self).__init__(**kwargs)
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title section
        title = Label(
            text='Client Appointment',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='60dp',
            color=hex_color(TEXT_COLOR)
        )
        
        # Client info card
        client_card = BoxLayout(
            orientation='vertical',
            size_hint_y=None,
            height='100dp',
            padding=15
        )
        with client_card.canvas.before:
            Color(rgba=hex_color('#F5F5F5'))
            Rectangle(pos=client_card.pos, size=client_card.size)
        
        client_name = Label(
            text='Project: Passion',
            font_size='18sp',
            bold=True,
            color=hex_color(TEXT_COLOR),
            size_hint_y=None,
            height='30dp',
            halign='left'
        )
        client_name.bind(size=client_name.setter('text_size'))
        
        client_details = Label(
            text='CCTV Installation and Configuration',
            font_size='16sp',
            color=hex_color(SUBTITLE_COLOR),
            size_hint_y=None,
            height='30dp',
            halign='left'
        )
        client_details.bind(size=client_details.setter('text_size'))
        
        client_card.add_widget(client_name)
        client_card.add_widget(client_details)
        
        # Action buttons
        client_btn = ModernButton(
            text='Schedule Appointment',
            size_hint=(1, None),
            height='56dp'
        )
        client_btn.bind(on_press=self.book_client_appointment)
        
        pay_btn = SecondaryButton(
            text='Process Payment',
            size_hint=(1, None),
            height='56dp'
        )
        pay_btn.bind(on_press=self.pay_advance)
        
        # Price info card
        price_card = BoxLayout(
            orientation='vertical',
            size_hint_y=None,
            height='80dp',
            padding=15
        )
        with price_card.canvas.before:
            Color(rgba=hex_color(PRIMARY_COLOR))
            Rectangle(pos=price_card.pos, size=price_card.size)
        
        price_title = Label(
            text='Service Cost',
            font_size='16sp',
            color=(1, 1, 1, 1),  # White
            size_hint_y=None,
            height='25dp'
        )
        
        price_amount = Label(
            text='$199.99',
            font_size='24sp',
            bold=True,
            color=(1, 1, 1, 1),  # White
            size_hint_y=None,
            height='35dp'
        )
        
        price_card.add_widget(price_title)
        price_card.add_widget(price_amount)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(Widget(size_hint_y=0.05))  # Spacer
        layout.add_widget(client_card)
        layout.add_widget(Widget(size_hint_y=0.05))  # Spacer
        layout.add_widget(price_card)
        layout.add_widget(Widget(size_hint_y=0.05))  # Spacer
        layout.add_widget(client_btn)
        layout.add_widget(pay_btn)
        layout.add_widget(Widget())  # Flexible spacer
        
        # Add background
        with self.canvas.before:
            Color(rgba=hex_color(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)
        self.add_debug_back_button(layout)
    
    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size
    
    def book_client_appointment(self, instance):
        self.show_popup('Booking appointment with client...')
    
    def pay_advance(self, instance):
        self.show_popup('Processing advance payment...')
    
    def show_popup(self, message):
        popup = Popup(
            title='Action',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

# Database Helper
class DatabaseHelper:
    def __init__(self):
        self.conn = sqlite3.connect('safecom.db')
        self.create_tables()

    def create_tables(self):
        cursor = self.conn.cursor()
        # Users table
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS users
        (id INTEGER PRIMARY KEY AUTOINCREMENT,
         username TEXT UNIQUE,
         password TEXT,
         role TEXT,
         location TEXT)
        ''')
        
        # Service requests table
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS service_requests
        (id INTEGER PRIMARY KEY AUTOINCREMENT,
         client_id INTEGER,
         service_type TEXT,
         location TEXT,
         preferred_time TEXT,
         status TEXT,
         assigned_to INTEGER,
         created_at TEXT,
         FOREIGN KEY (client_id) REFERENCES users (id),
         FOREIGN KEY (assigned_to) REFERENCES users (id))
        ''')
        self.conn.commit()

    def add_user(self, username, password, role, location=''):
        cursor = self.conn.cursor()
        try:
            cursor.execute('INSERT INTO users (username, password, role, location) VALUES (?, ?, ?, ?)',
                         (username, password, role, location))
            self.conn.commit()
            return True
        except sqlite3.IntegrityError:
            return False

    def verify_user(self, username, password):
        cursor = self.conn.cursor()
        cursor.execute('SELECT * FROM users WHERE username=? AND password=?', (username, password))
        user = cursor.fetchone()
        return user if user else None

    def add_service_request(self, client_id, service_type, location, preferred_time):
        cursor = self.conn.cursor()
        cursor.execute('''
        INSERT INTO service_requests 
        (client_id, service_type, location, preferred_time, status, created_at)
        VALUES (?, ?, ?, ?, ?, ?)
        ''', (client_id, service_type, location, preferred_time, 'pending', datetime.now().isoformat()))
        self.conn.commit()
        return cursor.lastrowid

    def get_pending_requests(self):
        cursor = self.conn.cursor()
        cursor.execute('''
        SELECT sr.*, u.username as client_name 
        FROM service_requests sr 
        JOIN users u ON sr.client_id = u.id 
        WHERE sr.status='pending'
        ''')
        return cursor.fetchall()

    def assign_request(self, request_id, employee_id):
        cursor = self.conn.cursor()
        cursor.execute('''
        UPDATE service_requests 
        SET assigned_to=?, status='assigned'
        WHERE id=?
        ''', (employee_id, request_id))
        self.conn.commit()

class LoginScreen(Screen):
    def __init__(self, db_helper, **kwargs):
        super(LoginScreen, self).__init__(**kwargs)
        self.db_helper = db_helper
        
        # Main layout with padding and spacing
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 60),
            spacing=20
        )
        
        # Title section
        title_layout = BoxLayout(
            orientation='vertical',
            size_hint=(1, 0.3),
            spacing=10
        )
        
        title = Label(
            text='SafeCom CCTV',
            font_size='36sp',
            bold=True,
            size_hint=(1, None),
            height='50dp',
            color=TEXT_COLOR
        )
        
        subtitle = Label(
            text='Secure your space with confidence',
            font_size='18sp',
            size_hint=(1, None),
            height='30dp',
            color=SUBTITLE_COLOR
        )
        
        title_layout.add_widget(title)
        title_layout.add_widget(subtitle)
        
        # Form section
        form_layout = BoxLayout(
            orientation='vertical',
            spacing=15,
            size_hint=(1, None),
            height='200dp'
        )
        
        username_label = Label(
            text='Username',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=SUBTITLE_COLOR,
            halign='left'
        )
        username_label.bind(size=username_label.setter('text_size'))
        
        self.username = ModernTextInput(
            hint_text='Enter your username'
        )
        
        password_label = Label(
            text='Password',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=SUBTITLE_COLOR,
            halign='left'
        )
        password_label.bind(size=password_label.setter('text_size'))
        
        self.password = ModernTextInput(
            hint_text='Enter your password',
            password=True
        )
        
        form_layout.add_widget(username_label)
        form_layout.add_widget(self.username)
        form_layout.add_widget(password_label)
        form_layout.add_widget(self.password)
        
        # Button section
        button_layout = BoxLayout(
            orientation='vertical',
            spacing=15,
            size_hint=(1, None),
            height='140dp'
        )
        
        login_btn = ModernButton(
            text='Login',
            size_hint=(1, None),
            height='56dp'
        )
        login_btn.bind(on_press=self.login)
        
        register_btn = SecondaryButton(
            text='Create Account',
            size_hint=(1, None),
            height='48dp'
        )
        register_btn.bind(on_press=self.show_register)
        
        button_layout.add_widget(login_btn)
        button_layout.add_widget(register_btn)
        
        # Add all sections to main layout
        layout.add_widget(Widget(size_hint_y=0.1))  # Top spacer
        layout.add_widget(title_layout)
        layout.add_widget(form_layout)
        layout.add_widget(button_layout)
        layout.add_widget(Widget())  # Bottom flexible spacer
        
        # Add background
        with self.canvas.before:
            Color(rgba=get_color_from_hex(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)
    
    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size

    def login(self, instance):
        user = self.db_helper.verify_user(self.username.text, self.password.text)
        if user:
            app = App.get_running_app()
            app.current_user = {
                'id': user[0],
                'username': user[1],
                'role': user[3],
                'location': user[4]
            }
            if user[3] == 'client':
                self.manager.current = 'client_main'
            elif user[3] == 'employee':
                self.manager.current = 'employee_dashboard'
            elif user[3] == 'admin':
                self.manager.current = 'admin_dashboard'
        else:
            self.show_popup('Invalid username or password')

    def show_register(self, instance):
        self.manager.current = 'register'

    def show_popup(self, message):
        popup = Popup(
            title='Message',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class RegisterScreen(Screen):
    def __init__(self, db_helper, **kwargs):
        super(RegisterScreen, self).__init__(**kwargs)
        self.db_helper = db_helper
        
        # Main layout with padding and spacing
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title section
        title_layout = BoxLayout(
            orientation='vertical',
            size_hint=(1, 0.2),
            spacing=10
        )
        
        title = Label(
            text='Create Account',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='50dp',
            color=hex_color(TEXT_COLOR)
        )
        
        subtitle = Label(
            text='Join SafeCom CCTV network',
            font_size='18sp',
            size_hint=(1, None),
            height='30dp',
            color=hex_color(SUBTITLE_COLOR)
        )
        
        title_layout.add_widget(title)
        title_layout.add_widget(subtitle)
        
        # Form section
        form_layout = BoxLayout(
            orientation='vertical',
            spacing=15,
            size_hint=(1, None),
            height='300dp'
        )
        
        # Username field
        username_label = Label(
            text='Username',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=hex_color(SUBTITLE_COLOR),
            halign='left'
        )
        username_label.bind(size=username_label.setter('text_size'))
        
        self.username = ModernTextInput(
            hint_text='Enter your username'
        )
        
        # Password field
        password_label = Label(
            text='Password',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=hex_color(SUBTITLE_COLOR),
            halign='left'
        )
        password_label.bind(size=password_label.setter('text_size'))
        
        self.password = ModernTextInput(
            hint_text='Enter your password',
            password=True
        )
        
        # Location field
        location_label = Label(
            text='Location',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=hex_color(SUBTITLE_COLOR),
            halign='left'
        )
        location_label.bind(size=location_label.setter('text_size'))
        
        self.location = ModernTextInput(
            hint_text='Enter your location'
        )
        
        # Role selection
        role_label = Label(
            text='Account Type',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=hex_color(SUBTITLE_COLOR),
            halign='left'
        )
        role_label.bind(size=role_label.setter('text_size'))
        
        self.role = ModernSpinner(
            text='Select Account Type',
            values=('client', 'employee'),
            size_hint=(1, None),
            height='48dp'
        )
        
        # Add form fields
        form_layout.add_widget(username_label)
        form_layout.add_widget(self.username)
        form_layout.add_widget(password_label)
        form_layout.add_widget(self.password)
        form_layout.add_widget(location_label)
        form_layout.add_widget(self.location)
        form_layout.add_widget(role_label)
        form_layout.add_widget(self.role)
        
        # Button section
        button_layout = BoxLayout(
            orientation='vertical',
            spacing=15,
            size_hint=(1, None),
            height='140dp'
        )
        
        register_btn = ModernButton(
            text='Create Account',
            size_hint=(1, None),
            height='56dp'
        )
        register_btn.bind(on_press=self.register)
        
        back_btn = SecondaryButton(
            text='Back to Login',
            size_hint=(1, None),
            height='48dp'
        )
        back_btn.bind(on_press=self.back_to_login)
        
        button_layout.add_widget(register_btn)
        button_layout.add_widget(back_btn)
        
        # Add all sections to main layout
        layout.add_widget(title_layout)
        layout.add_widget(form_layout)
        layout.add_widget(button_layout)
        layout.add_widget(Widget())  # Flexible spacer at bottom
        
        # Add background
        with self.canvas.before:
            Color(rgba=hex_color(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)

    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size

    def register(self, instance):
        if not all([self.username.text, self.password.text, self.location.text, self.role.text]):
            self.show_popup('Please fill all fields')
            return
        
        success = self.db_helper.add_user(
            self.username.text,
            self.password.text,
            self.role.text,
            self.location.text
        )
        
        if success:
            self.show_popup('Registration successful! Please login.')
            Clock.schedule_once(lambda dt: self.back_to_login(None), 2)
        else:
            self.show_popup('Error: Username may already exist')

    def back_to_login(self, instance):
        self.manager.current = 'login'

    def show_popup(self, message):
        popup = Popup(
            title='Message',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class ServiceRequestScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(ServiceRequestScreen, self).__init__(**kwargs)
        
        # Main layout with padding and spacing
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title with custom style
        title = Label(
            text='Request Service',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='60dp',
            color=TEXT_COLOR
        )
        
        # Service type selection with modern style
        service_label = Label(
            text='Select Service Type',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=SUBTITLE_COLOR,
            halign='left'
        )
        service_label.bind(size=service_label.setter('text_size'))
        
        self.service_type = ModernSpinner(
            text='Select Service Type',
            values=('Installation', 'Maintenance', 'Troubleshooting', 'IP Configuration'),
            size_hint=(1, None)
        )
        
        # Location input with modern style
        location_label = Label(
            text='Service Location',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=SUBTITLE_COLOR,
            halign='left'
        )
        location_label.bind(size=location_label.setter('text_size'))
        
        self.location = ModernTextInput(
            hint_text='Enter service location'
        )
        
        # Time input with modern style
        time_label = Label(
            text='Preferred Time',
            font_size='16sp',
            size_hint=(1, None),
            height='30dp',
            color=SUBTITLE_COLOR,
            halign='left'
        )
        time_label.bind(size=time_label.setter('text_size'))
        
        self.preferred_time = ModernTextInput(
            hint_text='YYYY-MM-DD HH:MM'
        )
        
        # Submit button with modern style
        submit_btn = ModernButton(
            text='Submit Request',
            size_hint=(1, None),
            height='56dp'
        )
        submit_btn.bind(on_press=self.submit_request)
        
        # Back button with secondary style
        back_btn = SecondaryButton(
            text='Back',
            size_hint=(1, None),
            height='48dp'
        )
        back_btn.bind(on_press=self.go_back)
        
        # Add widgets with proper spacing
        layout.add_widget(title)
        layout.add_widget(Widget(size_hint_y=0.1))  # Spacer
        layout.add_widget(service_label)
        layout.add_widget(self.service_type)
        layout.add_widget(location_label)
        layout.add_widget(self.location)
        layout.add_widget(time_label)
        layout.add_widget(self.preferred_time)
        layout.add_widget(Widget(size_hint_y=0.1))  # Spacer
        layout.add_widget(submit_btn)
        layout.add_widget(back_btn)
        layout.add_widget(Widget())  # Flexible spacer at bottom
        
        # Add background color
        with self.canvas.before:
            Color(rgba=get_color_from_hex(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)
        self.add_debug_back_button(layout)
    
    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size
    
    def submit_request(self, instance):
        if self.service_type.text == 'Select Service Type':
            self.show_popup('Please select a service type')
            return
        
        if not all([self.location.text, self.preferred_time.text]):
            self.show_popup('Please fill all fields')
            return
        
        app = App.get_running_app()
        if not app.current_user:
            self.show_popup('Please login first')
            self.manager.current = 'login'
            return
        
        # Add request to database
        request_id = app.db_helper.add_service_request(
            app.current_user['id'],
            self.service_type.text,
            self.location.text,
            self.preferred_time.text
        )
        
        if request_id:
            self.show_popup('Request submitted successfully!')
            Clock.schedule_once(lambda dt: self.go_back(None), 2)
        else:
            self.show_popup('Error submitting request')

    def go_back(self, instance):
        self.manager.current = 'client_dashboard'

    def show_popup(self, message):
        popup = Popup(
            title='Message',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class ServiceRequestCard(BoxLayout):
    def __init__(self, request_data, **kwargs):
        super(ServiceRequestCard, self).__init__(**kwargs)
        self.orientation = 'vertical'
        self.size_hint_y = None
        self.height = '160dp'
        self.padding = ('20dp', '15dp')
        self.spacing = '10dp'
        
        # Card background with shadow effect
        with self.canvas.before:
            Color(rgba=hex_color('#F5F5F5'))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        # Request type with icon
        type_layout = BoxLayout(size_hint_y=None, height='30dp')
        type_label = Label(
            text=f"Type: {request_data[1]}",
            color=hex_color(TEXT_COLOR),
            font_size='18sp',
            bold=True,
            halign='left',
            valign='middle'
        )
        type_label.bind(size=type_label.setter('text_size'))
        type_layout.add_widget(type_label)
        
        # Location
        location_label = Label(
            text=f"Location: {request_data[2]}",
            color=hex_color(SUBTITLE_COLOR),
            font_size='16sp',
            halign='left',
            valign='middle'
        )
        location_label.bind(size=location_label.setter('text_size'))
        
        # Time and status
        time_status_layout = BoxLayout(size_hint_y=None, height='30dp')
        time_label = Label(
            text=f"Time: {request_data[3]}",
            color=hex_color(SUBTITLE_COLOR),
            font_size='16sp',
            halign='left'
        )
        status_label = Label(
            text=f"Status: {request_data[4].upper()}",
            color=hex_color(ACCENT_COLOR),
            font_size='16sp',
            bold=True,
            halign='right'
        )
        time_status_layout.add_widget(time_label)
        time_status_layout.add_widget(status_label)
        
        # Add all elements to card
        self.add_widget(type_layout)
        self.add_widget(location_label)
        self.add_widget(time_status_layout)
    
    def _update_rect(self, instance, value):
        if hasattr(self, 'rect'):
            self.rect.pos = instance.pos
            self.rect.size = instance.size

class ViewRequestsScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(ViewRequestsScreen, self).__init__(**kwargs)
        self.layout = BoxLayout(
            orientation='vertical',
            padding=(20, 30),
            spacing=15
        )
        
        # Title
        title = Label(
            text='My Service Requests',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='60dp',
            color=TEXT_COLOR
        )
        
        # Scrollable requests view
        scroll = ScrollView(size_hint=(1, 1))
        self.requests_layout = GridLayout(
            cols=1,
            spacing=15,
            size_hint_y=None,
            padding=(10, 10)
        )
        self.requests_layout.bind(minimum_height=self.requests_layout.setter('height'))
        scroll.add_widget(self.requests_layout)
        
        # Back button with modern style
        back_btn = SecondaryButton(
            text='Back',
            size_hint=(1, None),
            height='48dp'
        )
        back_btn.bind(on_press=self.go_back)
        
        # Add widgets
        self.layout.add_widget(title)
        self.layout.add_widget(scroll)
        self.layout.add_widget(back_btn)
        
        # Add background
        with self.canvas.before:
            Color(rgba=get_color_from_hex(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(self.layout)
        self.add_debug_back_button(self.layout)
        
        # Schedule periodic updates
        Clock.schedule_interval(self.update_requests, 30)

    def on_enter(self):
        self.update_requests(0)

    def update_requests(self, dt):
        self.requests_layout.clear_widgets()
        app = App.get_running_app()
        if not app.current_user:
            return
        
        # Get user's requests from database
        cursor = app.db_helper.conn.cursor()
        cursor.execute('''
        SELECT id, service_type, location, preferred_time, status, created_at
        FROM service_requests
        WHERE client_id = ?
        ORDER BY created_at DESC
        ''', (app.current_user['id'],))
        
        requests = cursor.fetchall()
        
        if not requests:
            no_requests = Label(
                text='No service requests found',
                size_hint_y=None,
                height='40dp',
                color=SUBTITLE_COLOR
            )
            self.requests_layout.add_widget(no_requests)
            return
        
        for req in requests:
            card = ServiceRequestCard(req)
            self.requests_layout.add_widget(card)

    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size

    def go_back(self, instance):
        self.manager.current = 'client_dashboard'

class RequestAssignmentScreen(BaseScreen):
    def __init__(self, **kwargs):
        # Initialize the base class first
        BaseScreen.__init__(self, **kwargs)
        
        self.layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Add background first
        with self.canvas.before:
            Color(rgba=hex_color(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        
        # Title section
        title = Label(
            text='Available Requests',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='60dp',
            color=hex_color(TEXT_COLOR)
        )
        
        subtitle = Label(
            text='Assign and manage service requests',
            font_size='18sp',
            size_hint=(1, None),
            height='30dp',
            color=hex_color(SUBTITLE_COLOR)
        )
        
        # Status filters
        filter_box = BoxLayout(
            size_hint_y=None,
            height='40dp',
            spacing=10
        )
        
        all_btn = ModernButton(
            text='All',
            size_hint_x=0.25,
            background_color=hex_color(ACCENT_COLOR)
        )
        
        pending_btn = SecondaryButton(
            text='Pending',
            size_hint_x=0.25
        )
        
        urgent_btn = SecondaryButton(
            text='Urgent',
            size_hint_x=0.25
        )
        
        filter_box.add_widget(all_btn)
        filter_box.add_widget(pending_btn)
        filter_box.add_widget(urgent_btn)
        
        # Scrollable requests view with modern styling
        scroll = ScrollView(size_hint=(1, 1))
        self.requests_layout = GridLayout(
            cols=1,
            spacing=15,
            size_hint_y=None,
            padding=(0, 10)
        )
        self.requests_layout.bind(minimum_height=self.requests_layout.setter('height'))
        scroll.add_widget(self.requests_layout)
        
        # Navigation buttons
        button_box = BoxLayout(
            size_hint_y=None,
            height='56dp',
            spacing=10
        )
        
        refresh_btn = ModernButton(
            text='Refresh',
            size_hint_x=0.3
        )
        refresh_btn.bind(on_press=lambda x: self.update_requests(0))
        
        back_btn = SecondaryButton(
            text='Back to Dashboard',
            size_hint_x=0.7
        )
        back_btn.bind(on_press=self.go_back)
        
        button_box.add_widget(refresh_btn)
        button_box.add_widget(back_btn)
        
        # Add all widgets to main layout
        self.layout.add_widget(title)
        self.layout.add_widget(subtitle)
        self.layout.add_widget(Widget(size_hint_y=0.05))  # Spacer
        self.layout.add_widget(filter_box)
        self.layout.add_widget(scroll)
        self.layout.add_widget(button_box)
        
        self.add_widget(self.layout)
        self.add_debug_back_button(self.layout)
        
        # Schedule periodic updates
        Clock.schedule_interval(self.update_requests, 30)  # Update every 30 seconds

    def on_enter(self):
        self.update_requests(0)

    def update_requests(self, dt):
        self.requests_layout.clear_widgets()
        app = App.get_running_app()
        if not app.current_user or app.current_user['role'] != 'employee':
            return
        
        # Get pending requests from database
        pending_requests = app.db_helper.get_pending_requests()
        
        if not pending_requests:
            no_requests = Label(
                text='No pending service requests',
                size_hint_y=None,
                height='40dp',
                color=hex_color(SUBTITLE_COLOR)
            )
            self.requests_layout.add_widget(no_requests)
            return
        
        for req in pending_requests:
            # Modern card layout
            request_card = BoxLayout(
                orientation='vertical',
                size_hint_y=None,
                height='160dp',
                padding=15,
                spacing=10
            )
            with request_card.canvas.before:
                Color(rgba=hex_color('#F5F5F5'))
                Rectangle(pos=request_card.pos, size=request_card.size)
            
            # Header with client name and type
            header = BoxLayout(
                size_hint_y=None,
                height='30dp'
            )
            
            client_name = Label(
                text=f"Client: {req[-1]}",
                font_size='18sp',
                bold=True,
                color=hex_color(TEXT_COLOR),
                size_hint_x=0.6,
                halign='left'
            )
            client_name.bind(size=client_name.setter('text_size'))
            
            request_type = Label(
                text=f"Type: {req[2]}",
                font_size='16sp',
                color=hex_color(ACCENT_COLOR),
                size_hint_x=0.4,
                halign='right'
            )
            request_type.bind(size=request_type.setter('text_size'))
            
            header.add_widget(client_name)
            header.add_widget(request_type)
            
            # Details
            details = BoxLayout(
                orientation='vertical',
                size_hint_y=None,
                height='50dp',
                spacing=5
            )
            
            location = Label(
                text=f"📍 {req[3]}",
                font_size='16sp',
                color=hex_color(SUBTITLE_COLOR),
                halign='left'
            )
            location.bind(size=location.setter('text_size'))
            
            time = Label(
                text=f"🕒 {req[4]}",
                font_size='16sp',
                color=hex_color(SUBTITLE_COLOR),
                halign='left'
            )
            time.bind(size=time.setter('text_size'))
            
            details.add_widget(location)
            details.add_widget(time)
            
            # Action buttons
            button_box = BoxLayout(
                size_hint_y=None,
                height='40dp',
                spacing=10
            )
            
            accept_btn = ModernButton(
                text='Accept Request',
                size_hint_x=0.7
            )
            accept_btn.bind(on_press=lambda x, req_id=req[0]: self.accept_request(req_id))
            
            details_btn = SecondaryButton(
                text='Details',
                size_hint_x=0.3
            )
            details_btn.bind(on_press=lambda x: self.show_popup('Loading details...'))
            
            button_box.add_widget(accept_btn)
            button_box.add_widget(details_btn)
            
            # Add all elements to card
            request_card.add_widget(header)
            request_card.add_widget(details)
            request_card.add_widget(button_box)
            
            self.requests_layout.add_widget(request_card)

    def accept_request(self, request_id):
        app = App.get_running_app()
        if not app.current_user:
            return
        
        app.db_helper.assign_request(request_id, app.current_user['id'])
        self.show_popup('Request accepted successfully!')
        self.update_requests(0)

    def go_back(self, instance):
        self.manager.current = 'employee_dashboard'

    def show_popup(self, message):
        popup = Popup(
            title='Message',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class ClientDashboardScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(ClientDashboardScreen, self).__init__(**kwargs)
        
        # Main layout with padding and spacing
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title with custom style
        title = Label(
            text='Client Dashboard',
            font_size='32sp',
            bold=True,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        # Welcome message with subtitle color
        welcome_msg = Label(
            text='Welcome to SafeCom CCTV',
            font_size='20sp',
            size_hint=(1, 0.15),
            color=SUBTITLE_COLOR
        )
        
        # Service request button with modern style
        request_btn = ModernButton(
            text='Request New Service',
            size_hint=(1, None),
            height='56dp'
        )
        request_btn.bind(on_press=self.request_service)
        
        # View requests button with secondary style
        view_btn = SecondaryButton(
            text='View My Requests',
            size_hint=(1, None),
            height='56dp'
        )
        view_btn.bind(on_press=self.view_requests)
        
        # Spacer for better layout
        spacer = Widget(size_hint_y=0.2)
        
        # Add widgets to layout
        layout.add_widget(spacer)  # Top spacer
        layout.add_widget(title)
        layout.add_widget(welcome_msg)
        layout.add_widget(request_btn)
        layout.add_widget(view_btn)
        layout.add_widget(Widget())  # Bottom flexible spacer
        
        # Add layout to screen with background color
        with self.canvas.before:
            Color(rgba=hex_color(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)
        self.add_debug_back_button(layout)
    
    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size
    
    def request_service(self, instance):
        self.manager.current = 'service_request'
    
    def view_requests(self, instance):
        self.manager.current = 'view_requests'

class EmployeeDashboardScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(EmployeeDashboardScreen, self).__init__(**kwargs)
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title
        title = Label(
            text='Employee Dashboard',
            font_size='32sp',
            bold=True,
            size_hint=(1, None),
            height='60dp',
            color=hex_color(TEXT_COLOR)
        )
        
        # Welcome message with status card
        status_card = BoxLayout(
            orientation='vertical',
            size_hint_y=None,
            height='100dp',
            padding=15
        )
        with status_card.canvas.before:
            Color(rgba=hex_color('#F5F5F5'))
            Rectangle(pos=status_card.pos, size=status_card.size)
        
        welcome_msg = Label(
            text='Welcome back, Employee',
            font_size='20sp',
            bold=True,
            color=hex_color(TEXT_COLOR),
            size_hint_y=None,
            height='30dp',
            halign='left'
        )
        
        status_msg = Label(
            text='Active - On Duty',
            font_size='16sp',
            color=hex_color(ACCENT_COLOR),
            size_hint_y=None,
            height='30dp',
            halign='left'
        )
        
        status_card.add_widget(welcome_msg)
        status_card.add_widget(status_msg)
        
        # Action buttons with modern style
        assigned_btn = ModernButton(
            text='View Assigned Requests',
            size_hint=(1, None),
            height='56dp',
            background_color=hex_color(ACCENT_COLOR)
        )
        assigned_btn.bind(on_press=self.view_assigned_requests)
        
        # Statistics card
        stats_card = BoxLayout(
            orientation='vertical',
            size_hint_y=None,
            height='120dp',
            padding=15,
            spacing=10
        )
        with stats_card.canvas.before:
            Color(rgba=hex_color(PRIMARY_COLOR))
            Rectangle(pos=stats_card.pos, size=stats_card.size)
        
        stats_title = Label(
            text='Today\'s Statistics',
            font_size='18sp',
            bold=True,
            color=(1, 1, 1, 1),
            size_hint_y=None,
            height='30dp'
        )
        
        stats_layout = BoxLayout(spacing=10)
        completed = Label(
            text='Completed\n5',
            halign='center',
            color=(1, 1, 1, 1),
            bold=True
        )
        pending = Label(
            text='Pending\n3',
            halign='center',
            color=(1, 1, 1, 1),
            bold=True
        )
        stats_layout.add_widget(completed)
        stats_layout.add_widget(pending)
        
        stats_card.add_widget(stats_title)
        stats_card.add_widget(stats_layout)
        
        # Quick action buttons
        profile_btn = SecondaryButton(
            text='View Profile',
            size_hint=(1, None),
            height='56dp'
        )
        profile_btn.bind(on_press=self.view_profile)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(status_card)
        layout.add_widget(stats_card)
        layout.add_widget(assigned_btn)
        layout.add_widget(profile_btn)
        
        self.add_widget(layout)
        self.add_debug_back_button(layout)
    
    def view_assigned_requests(self, instance):
        self.manager.current = 'request_assignment'
    
    def view_profile(self, instance):
        self.show_popup('Viewing profile is not implemented yet.')
    
    def show_popup(self, message):
        popup = Popup(
            title='Info',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class AdminDashboardScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(AdminDashboardScreen, self).__init__(**kwargs)
        layout = BoxLayout(
            orientation='vertical',
            padding=(30, 40),
            spacing=20
        )
        
        # Title with admin badge
        title_box = BoxLayout(
            orientation='horizontal',
            size_hint_y=None,
            height='60dp'
        )
        
        title = Label(
            text='Admin Dashboard',
            font_size='32sp',
            bold=True,
            size_hint_x=0.7,
            color=hex_color(TEXT_COLOR)
        )
        
        admin_badge = BoxLayout(
            size_hint=(None, None),
            size=('100dp', '30dp'),
            padding=5,
            pos_hint={'center_y': 0.5}
        )
        with admin_badge.canvas.before:
            Color(rgba=hex_color(ACCENT_COLOR))
            self.badge_rect = Rectangle(pos=admin_badge.pos, size=admin_badge.size)
            admin_badge.bind(pos=self._update_badge_rect, size=self._update_badge_rect)
        
        admin_label = Label(
            text='ADMIN',
            font_size='14sp',
            bold=True,
            color=(1, 1, 1, 1)
        )
        admin_badge.add_widget(admin_label)
        
        title_box.add_widget(title)
        title_box.add_widget(admin_badge)
        
        # System status card
        status_card = BoxLayout(
            orientation='vertical',
            size_hint_y=None,
            height='100dp',
            padding=15
        )
        with status_card.canvas.before:
            Color(rgba=hex_color('#F5F5F5'))
            self.status_rect = Rectangle(pos=status_card.pos, size=status_card.size)
            status_card.bind(pos=self._update_status_rect, size=self._update_status_rect)
        
        status_title = Label(
            text='System Status',
            font_size='18sp',
            bold=True,
            color=hex_color(TEXT_COLOR),
            size_hint_y=None,
            height='30dp',
            halign='left'
        )
        
        status_details = Label(
            text='All systems operational | Last checked: 2min ago',
            font_size='16sp',
            color=hex_color('#4CAF50'),
            size_hint_y=None,
            height='30dp',
            halign='left'
        )
        
        status_card.add_widget(status_title)
        status_card.add_widget(status_details)
        
        # Add widgets to layout
        layout.add_widget(title_box)
        layout.add_widget(status_card)
        
        # Add background color
        with self.canvas.before:
            Color(rgba=hex_color(BACKGROUND_COLOR))
            self.rect = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_rect, size=self._update_rect)
        
        self.add_widget(layout)
        self.add_debug_back_button(layout)

    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size
    
    def _update_badge_rect(self, instance, value):
        self.badge_rect.pos = instance.pos
        self.badge_rect.size = instance.size
    
    def _update_status_rect(self, instance, value):
        self.status_rect.pos = instance.pos
        self.status_rect.size = instance.size

class ClientMainScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(ClientMainScreen, self).__init__(**kwargs)
        
        # Main layout
        main_layout = BoxLayout(orientation='vertical')
        
        # Content area (top section)
        self.content_area = ScreenManager()
        
        # Create sub-screens
        home_screen = BoxLayout(orientation='vertical', padding=20, spacing=10)
        
        # Home screen content
        title = Label(
            text='Welcome to SafeCom CCTV',
            font_size=24,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        book_btn = Button(
            text='Book Appointment',
            size_hint=(1, 0.15),
            background_color=PRIMARY_COLOR
        )
        book_btn.bind(on_press=self.go_to_booking)
        
        service_btn = Button(
            text='Book Service',
            size_hint=(1, 0.15),
            background_color=PRIMARY_COLOR
        )
        service_btn.bind(on_press=self.go_to_service)
        
        products_btn = Button(
            text='Buy Products',
            size_hint=(1, 0.15),
            background_color=SECONDARY_COLOR
        )
        products_btn.bind(on_press=self.go_to_products)
        
        # Add widgets to home screen
        home_screen.add_widget(title)
        home_screen.add_widget(book_btn)
        home_screen.add_widget(service_btn)
        home_screen.add_widget(products_btn)
        
        # Create screens for content area
        screen1 = Screen(name='home')
        screen1.add_widget(home_screen)
        
        screen2 = ServiceScreen(name='service')
        screen3 = ViewRequestsScreen(name='requests')
        
        # Add screens to content area
        self.content_area.add_widget(screen1)
        self.content_area.add_widget(screen2)
        self.content_area.add_widget(screen3)
        
        # Navigation tabs (bottom section)
        nav_tabs = BoxLayout(
            size_hint=(1, 0.1),
            padding=5,
            spacing=5
        )
        
        # Create tab buttons
        home_tab = Button(
            text='Home',
            background_color=PRIMARY_COLOR
        )
        service_tab = Button(
            text='Services',
            background_color=PRIMARY_COLOR
        )
        requests_tab = Button(
            text='My Requests',
            background_color=PRIMARY_COLOR
        )
        
        # Bind tab buttons
        home_tab.bind(on_press=lambda x: self.switch_screen('home'))
        service_tab.bind(on_press=lambda x: self.switch_screen('service'))
        requests_tab.bind(on_press=lambda x: self.switch_screen('requests'))
        
        # Add tabs to navigation
        nav_tabs.add_widget(home_tab)
        nav_tabs.add_widget(service_tab)
        nav_tabs.add_widget(requests_tab)
        
        # Add content area and navigation to main layout
        main_layout.add_widget(self.content_area)
        main_layout.add_widget(nav_tabs)
        
        self.add_widget(main_layout)
        self.add_debug_back_button(main_layout)
    
    def switch_screen(self, screen_name):
        self.content_area.current = screen_name
    
    def go_to_booking(self, instance):
        self.switch_screen('service')
        
    def go_to_service(self, instance):
        self.switch_screen('service')
        
    def go_to_products(self, instance):
        self.show_popup('Products section coming soon!')
    
    def show_popup(self, message):
        popup = Popup(
            title='Info',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

# Debug navigation screen for development
class DebugNavigationScreen(BaseScreen):
    def __init__(self, **kwargs):
        super(DebugNavigationScreen, self).__init__(**kwargs)
        layout = BoxLayout(orientation='vertical', padding=20, spacing=10)
        
        # Title
        title = Label(
            text='Debug Navigation',
            font_size=24,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        # Navigation buttons
        buttons = [
            ('Client Dashboard', 'client_dashboard'),
            ('Employee Dashboard', 'employee_dashboard'),
            ('Admin Dashboard', 'admin_dashboard'),
            ('Service Request', 'service_request'),
            ('View Requests', 'view_requests'),
            ('Request Assignment', 'request_assignment'),
            ('Client Main', 'client_main'),
            ('Safecam', 'safecam'),
            ('Service', 'service'),
            ('Troubleshooting', 'troubleshooting'),
            ('Client Appointment', 'client_appointment')
        ]
        
        for text, screen_name in buttons:
            btn = ModernButton(
                text=text,
                size_hint=(1, None),
                height='56dp'
            )
            btn.bind(on_press=lambda x, sn=screen_name: self.switch_screen(sn))
            layout.add_widget(btn)
        
        # Back button
        back_btn = SecondaryButton(
            text='Back to Login',
            size_hint=(1, None),
            height='48dp'
        )
        back_btn.bind(on_press=self.back_to_login)
        layout.add_widget(back_btn)
        
        self.add_widget(layout)
    
    def switch_screen(self, screen_name):
        self.manager.current = screen_name
    
    def back_to_login(self, instance):
        self.manager.current = 'login'

# Custom button classes
class ModernButton(Button):
    def __init__(self, **kwargs):
        super(ModernButton, self).__init__(**kwargs)
        self.background_normal = ''  # Remove default background
        self.border = (0, 0, 0, 0)  # Remove border
        self.background_color = hex_color(ACCENT_COLOR)  # Default color
        self.color = hex_color('#FFFFFF')  # Text color
        self.font_size = '18sp'  # Larger font size
        self.bold = True  # Bold text
        self.radius = [10]  # Rounded corners
        
        # Custom style updates
        self.padding = ('20dp', '10dp')
        self.height = '48dp'  # Standard Material height

class SecondaryButton(ModernButton):
    def __init__(self, **kwargs):
        super(SecondaryButton, self).__init__(**kwargs)
        self.background_color = hex_color(SECONDARY_COLOR)  # Secondary color

class ModernTextInput(TextInput):
    def __init__(self, **kwargs):
        super(ModernTextInput, self).__init__(**kwargs)
        self.background_normal = ''
        self.background_color = (0.95, 0.95, 0.95, 1)  # Light grey background
        self.cursor_color = hex_color(ACCENT_COLOR)
        self.foreground_color = hex_color(TEXT_COLOR)
        self.padding = [20, 15, 20, 15]  # Left, Top, Right, Bottom
        self.font_size = '18sp'
        self.multiline = False
        self.size_hint_y = None
        self.height = '48dp'

class ModernSpinner(Spinner):
    def __init__(self, **kwargs):
        super(ModernSpinner, self).__init__(**kwargs)
        self.background_normal = ''
        self.background_color = hex_color(PRIMARY_COLOR)
        self.color = hex_color('#FFFFFF')
        self.size_hint_y = None
        self.height = '48dp'
        self.font_size = '18sp'
        self.bold = True
        self.padding = [20, 0]

# CCTV App
class CCTVApp(App):
    def build(self):
        # Initialize database helper
        self.db_helper = DatabaseHelper()
        self.current_user = None
        
        # Create the screen manager
        sm = ScreenManager()
        
        # Create all screens
        login = LoginScreen(self.db_helper, name='login')
        register = RegisterScreen(self.db_helper, name='register')
        client_dashboard = ClientDashboardScreen(name='client_dashboard')
        employee_dashboard = EmployeeDashboardScreen(name='employee_dashboard')
        admin_dashboard = AdminDashboardScreen(name='admin_dashboard')
        service_request = ServiceRequestScreen(name='service_request')
        view_requests = ViewRequestsScreen(name='view_requests')
        request_assignment = RequestAssignmentScreen(name='request_assignment')
        client_main = ClientMainScreen(name='client_main')
        safecam = SafecamScreen(name='safecam')
        service = ServiceScreen(name='service')
        troubleshooting = TroubleshootingScreen(name='troubleshooting')
        client_appointment = ClientAppointmentScreen(name='client_appointment')
        debug_nav = DebugNavigationScreen(name='debug_nav')
        
        # Add all screens to the screen manager
        sm.add_widget(login)
        sm.add_widget(register)
        sm.add_widget(client_dashboard)
        sm.add_widget(employee_dashboard)
        sm.add_widget(admin_dashboard)
        sm.add_widget(service_request)
        sm.add_widget(view_requests)
        sm.add_widget(request_assignment)
        sm.add_widget(client_main)
        sm.add_widget(safecam)
        sm.add_widget(service)
        sm.add_widget(troubleshooting)
        sm.add_widget(client_appointment)
        sm.add_widget(debug_nav)
        
        # Set initial screen
        if DEBUG_MODE:
            sm.current = 'debug_nav'
        else:
            sm.current = 'login'
        
        return sm

if __name__ == '__main__':
    CCTVApp().run()