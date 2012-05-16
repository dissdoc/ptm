class AddNameDescriptionToAssortment < ActiveRecord::Migration
  def change
    add_column :assortments, :name, :string

    add_column :assortments, :description, :text

  end
end
