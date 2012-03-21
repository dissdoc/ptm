class AddGenerateToPhoto < ActiveRecord::Migration
  def change
    add_column :photos, :generate, :datetime

  end
end
