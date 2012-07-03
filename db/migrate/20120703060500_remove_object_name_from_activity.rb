class RemoveObjectNameFromActivity < ActiveRecord::Migration
  def up
    remove_column :activities, :object_name
    remove_column :activities, :object_link
    remove_column :activities, :object_name2
    remove_column :activities, :object_link2
    remove_column :activities, :action2
  end

  def down
    add_column :activities, :object_link, :string
    add_column :activities, :object_name, :string
    add_column :activities, :object_name2, :string
    add_column :activities, :object_link2, :string
    add_column :activities, :action2, :string
  end
end
