class AddPolymorphicToNotes < ActiveRecord::Migration
  def change
    add_column :notes, :notable_id, :integer

    add_column :notes, :notable_type, :string

  end
end
