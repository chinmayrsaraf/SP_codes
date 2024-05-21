import tkinter as tk
from tkinter import filedialog, messagebox, font
import os

class TextEditor:
    def __init__(self, root):
        self.root = root
        self.root.title("Notepad")
        self.root.geometry("600x400")

        self.text_area = tk.Text(self.root, undo=True)
        self.text_area.pack(fill=tk.BOTH, expand=1)

        self.current_file = None

        self.create_menu()
        
    def create_menu(self):
        menu_bar = tk.Menu(self.root)
        self.root.config(menu=menu_bar)

        file_menu = tk.Menu(menu_bar, tearoff=0)
        menu_bar.add_cascade(label="File", menu=file_menu)
        file_menu.add_command(label="New", command=self.new_file)
        file_menu.add_command(label="Open", command=self.open_file)
        file_menu.add_command(label="Save", command=self.save_file)
        file_menu.add_command(label="Save As", command=self.save_as_file)
        file_menu.add_separator()
        file_menu.add_command(label="Exit", command=self.exit_app)

        edit_menu = tk.Menu(menu_bar, tearoff=0)
        menu_bar.add_cascade(label="Edit", menu=edit_menu)
        edit_menu.add_command(label="Undo", command=self.text_area.edit_undo)
        edit_menu.add_command(label="Redo", command=self.text_area.edit_redo)
        edit_menu.add_separator()
        edit_menu.add_command(label="Cut", command=self.cut_text)
        edit_menu.add_command(label="Copy", command=self.copy_text)
        edit_menu.add_command(label="Paste", command=self.paste_text)

        format_menu = tk.Menu(menu_bar, tearoff=0)
        menu_bar.add_cascade(label="Format", menu=format_menu)
        format_menu.add_command(label="Font", command=self.select_font)
        format_menu.add_command(label="Font Size", command=self.select_font_size)
        format_menu.add_command(label="Bold", command=self.toggle_bold)
        format_menu.add_command(label="Italic", command=self.toggle_italic)

    def new_file(self):
        self.text_area.delete(1.0, tk.END)
        self.current_file = None

    def open_file(self):
        file_path = filedialog.askopenfilename(defaultextension=".txt",
                                               filetypes=[("Text files", "*.txt"), ("All files", "*.*")])
        if file_path:
            with open(file_path, "r") as file:
                self.text_area.delete(1.0, tk.END)
                self.text_area.insert(tk.END, file.read())
            self.current_file = file_path

    def save_file(self):
        if self.current_file:
            try:
                with open(self.current_file, "w") as file:
                    file.write(self.text_area.get(1.0, tk.END))
            except Exception as e:
                messagebox.showerror("Save Error", str(e))
        else:
            self.save_as_file()

    def save_as_file(self):
        file_path = filedialog.asksaveasfilename(defaultextension=".txt",
                                                 filetypes=[("Text files", "*.txt"), ("All files", "*.*")])
        if file_path:
            try:
                with open(file_path, "w") as file:
                    file.write(self.text_area.get(1.0, tk.END))
                self.current_file = file_path
            except Exception as e:
                messagebox.showerror("Save As Error", str(e))

    def exit_app(self):
        self.root.quit()

    def cut_text(self):
        self.text_area.event_generate("<<Cut>>")

    def copy_text(self):
        self.text_area.event_generate("<<Copy>>")

    def paste_text(self):
        self.text_area.event_generate("<<Paste>>")

    def select_font(self):
        fonts = list(font.families())
        selected_font = tk.simpledialog.askstring("Select Font", "Font:", initialvalue=self.text_area.cget("font"))
        if selected_font in fonts:
            self.text_area.config(font=(selected_font, self.text_area.cget("font")[1]))

    def select_font_size(self):
        size = tk.simpledialog.askinteger("Font Size", "Enter Font Size:", initialvalue=self.text_area.cget("font")[1])
        if size:
            self.text_area.config(font=(self.text_area.cget("font")[0], size))

    def toggle_bold(self):
        current_font = font.Font(font=self.text_area.cget("font"))
        if current_font.actual()["weight"] == "bold":
            new_font = current_font.copy()
            new_font.config(weight="normal")
        else:
            new_font = current_font.copy()
            new_font.config(weight="bold")
        self.text_area.config(font=new_font)

    def toggle_italic(self):
        current_font = font.Font(font=self.text_area.cget("font"))
        if current_font.actual()["slant"] == "italic":
            new_font = current_font.copy()
            new_font.config(slant="roman")
        else:
            new_font = current_font.copy()
            new_font.config(slant="italic")
        self.text_area.config(font=new_font)

if __name__ == "__main__":
    root = tk.Tk()
    app = TextEditor(root)
    root.mainloop()
