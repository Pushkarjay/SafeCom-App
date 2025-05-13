from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.popup import Popup
from kivy.uix.tabbedpanel import TabbedPanel, TabbedPanelItem
from kivy.core.window import Window
from kivy.utils import get_color_from_hex

# Set window size for desktop testing
Window.size = (360, 640)  # Typical phone size

# Updated color scheme
PRIMARY_COLOR = get_color_from_hex('#90CAF9')  # Light Blue
SECONDARY_COLOR = get_color_from_hex('#FFD54F')  # Light Amber
BACKGROUND_COLOR = get_color_from_hex('#FFFFFF')  # White
TEXT_COLOR = get_color_from_hex('#000000')  # Black
ACTIVE_TAB_COLOR = get_color_from_hex('#42A5F5')  # Highlighted Blue

class SafecamScreen(Screen):
    def __init__(self, **kwargs):
        super(SafecamScreen, self).__init__(**kwargs)
        layout = BoxLayout(orientation='vertical', padding=20, spacing=10)
        
        # Title
        title = Label(
            text='Screen 1: Safecam',
            font_size=24,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        # Appointment label
        appointment_label = Label(
            text='Appointment: Amber',
            font_size=18,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        # Book Appointment button
        book_btn = Button(
            text='Book Appointment',
            size_hint=(1, 0.15),
            background_color=PRIMARY_COLOR
        )
        book_btn.bind(on_press=self.book_appointment)
        
        # Buy Product button
        buy_btn = Button(
            text='Buy Product',
            size_hint=(1, 0.15),
            background_color=SECONDARY_COLOR
        )
        buy_btn.bind(on_press=self.buy_product)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(appointment_label)
        layout.add_widget(book_btn)
        layout.add_widget(buy_btn)
        
        self.add_widget(layout)
    
    def book_appointment(self, instance):
        self.show_popup('Booking appointment for Amber...')
    
    def buy_product(self, instance):
        self.show_popup('Initiating product purchase...')
    
    def show_popup(self, message):
        popup = Popup(
            title='Action',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class ServiceScreen(Screen):
    def __init__(self, **kwargs):
        super(ServiceScreen, self).__init__(**kwargs)
        layout = BoxLayout(orientation='vertical', padding=20, spacing=10)
        
        # Title
        title = Label(
            text='Screen 2: Service & New CCTV',
            font_size=24,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        # Book Service button
        service_btn = Button(
            text='Book Service Appointment',
            size_hint=(1, 0.15),
            background_color=PRIMARY_COLOR
        )
        service_btn.bind(on_press=self.book_service)
        
        # New CCTV Installation button
        install_btn = Button(
            text='New CCTV Installation',
            size_hint=(1, 0.15),
            background_color=SECONDARY_COLOR
        )
        install_btn.bind(on_press=self.new_installation)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(service_btn)
        layout.add_widget(install_btn)
        
        self.add_widget(layout)
    
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

class TroubleshootingScreen(Screen):
    def __init__(self, **kwargs):
        super(TroubleshootingScreen, self).__init__(**kwargs)
        layout = BoxLayout(orientation='vertical', padding=20, spacing=10)
        
        # Title
        title = Label(
            text='Screen 3: Troubleshooting',
            font_size=24,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        # Issues list
        issues_label = Label(
            text='Issue 1: CCTV Camera Not Working\nIssue 2: CCTV Recording Mode\nIssue 3: Other Issue',
            font_size=16,
            size_hint=(1, 0.3),
            halign='left',  # Corrected alignment
            valign='middle',  # Corrected alignment
            color=TEXT_COLOR
        )
        issues_label.bind(size=issues_label.setter('text_size'))
        
        # Book Troubleshooting button
        trouble_btn = Button(
            text='Book Troubleshooting Appointment',
            size_hint=(1, 0.15),
            background_color=PRIMARY_COLOR
        )
        trouble_btn.bind(on_press=self.book_troubleshooting)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(issues_label)
        layout.add_widget(trouble_btn)
        
        self.add_widget(layout)
    
    def book_troubleshooting(self, instance):
        self.show_popup('Booking troubleshooting appointment...')
    
    def show_popup(self, message):
        popup = Popup(
            title='Action',
            content=Label(text=message),
            size_hint=(0.8, 0.3)
        )
        popup.open()

class ClientAppointmentScreen(Screen):
    def __init__(self, **kwargs):
        super(ClientAppointmentScreen, self).__init__(**kwargs)
        layout = BoxLayout(orientation='vertical', padding=20, spacing=10)
        
        # Title
        title = Label(
            text='Screen 4: Client Appointment',
            font_size=24,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        # Client note
        client_label = Label(
            text='Note: For project/client "Passion"',
            font_size=18,
            size_hint=(1, 0.2),
            color=TEXT_COLOR
        )
        
        # Book Client Appointment button
        client_btn = Button(
            text='Book Appointment with Client',
            size_hint=(1, 0.15),
            background_color=PRIMARY_COLOR
        )
        client_btn.bind(on_press=self.book_client_appointment)
        
        # Pay Advance button
        pay_btn = Button(
            text='Pay Advance for Troubleshooting',
            size_hint=(1, 0.15),
            background_color=SECONDARY_COLOR
        )
        pay_btn.bind(on_press=self.pay_advance)
        
        # Add widgets to layout
        layout.add_widget(title)
        layout.add_widget(client_label)
        layout.add_widget(client_btn)
        layout.add_widget(pay_btn)
        
        self.add_widget(layout)
    
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

class CCTVApp(App):
    def build(self):
        # Create the screen manager
        sm = ScreenManager()
        
        # Create screens
        safecam_screen = SafecamScreen(name='safecam')
        service_screen = ServiceScreen(name='service')
        troubleshooting_screen = TroubleshootingScreen(name='troubleshooting')
        client_screen = ClientAppointmentScreen(name='client')
        
        # Add screens to the screen manager
        sm.add_widget(safecam_screen)
        sm.add_widget(service_screen)
        sm.add_widget(troubleshooting_screen)
        sm.add_widget(client_screen)
        
        # Create main layout
        main_layout = BoxLayout(orientation='vertical')
        
        # Add screen manager to main layout
        main_layout.add_widget(sm)
        
        # Create navigation tabs
        navigation = BoxLayout(
            size_hint=(1, 0.1),
            padding=5,
            spacing=5
            # Removed background_color as it is not a valid property for BoxLayout
        )
        
        # Create tab buttons
        safecam_tab = Button(text='Safecam', background_color=PRIMARY_COLOR)
        service_tab = Button(text='Service', background_color=PRIMARY_COLOR)
        trouble_tab = Button(text='Troubleshoot', background_color=PRIMARY_COLOR)
        client_tab = Button(text='Client', background_color=PRIMARY_COLOR)
        
        # Highlight the active tab
        def update_tab_colors(active_tab):
            for tab in [safecam_tab, service_tab, trouble_tab, client_tab]:
                tab.background_color = PRIMARY_COLOR
            active_tab.background_color = ACTIVE_TAB_COLOR
        
        # Bind tab buttons to screen changes and highlight effect
        safecam_tab.bind(on_press=lambda x: (setattr(sm, 'current', 'safecam'), update_tab_colors(safecam_tab)))
        service_tab.bind(on_press=lambda x: (setattr(sm, 'current', 'service'), update_tab_colors(service_tab)))
        trouble_tab.bind(on_press=lambda x: (setattr(sm, 'current', 'troubleshooting'), update_tab_colors(trouble_tab)))
        client_tab.bind(on_press=lambda x: (setattr(sm, 'current', 'client'), update_tab_colors(client_tab)))
        
        # Add tabs to navigation
        navigation.add_widget(safecam_tab)
        navigation.add_widget(service_tab)
        navigation.add_widget(trouble_tab)
        navigation.add_widget(client_tab)
        
        # Add navigation to main layout
        main_layout.add_widget(navigation)
        
        # Set initial active tab
        update_tab_colors(safecam_tab)
        
        return main_layout

if __name__ == '__main__':
    CCTVApp().run()